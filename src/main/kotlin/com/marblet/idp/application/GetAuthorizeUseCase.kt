package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.ConsentRequired
import com.marblet.idp.application.error.AuthorizationApplicationError.LoginRequired
import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.ConsentedScopes
import com.marblet.idp.domain.model.IdTokenPayload
import com.marblet.idp.domain.model.Prompt
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.ValidatedAuthorizationRequest
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.ConsentRepository
import com.marblet.idp.domain.repository.UserRepository
import com.marblet.idp.domain.service.AccessTokenConverter
import com.marblet.idp.domain.service.IdTokenConverter
import org.springframework.stereotype.Service

@Service
class GetAuthorizeUseCase(
    private val consentUrlGenerator: ConsentUrlGenerator,
    private val loginUrlGenerator: LoginUrlGenerator,
    private val clientCallbackUrlGenerator: ClientCallbackUrlGenerator,
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val consentRepository: ConsentRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val accessTokenConverter: AccessTokenConverter,
    private val idTokenConverter: IdTokenConverter,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        prompt: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        val request =
            ValidatedAuthorizationRequest.create(
                client = clientRepository.get(clientId),
                user = loginCookie?.let { userRepository.get(loginCookie) },
                responseTypeInput = responseType,
                redirectUri = redirectUri,
                scope = scope,
                prompt = prompt,
            ).fold({
                return when (it) {
                    ClientNotExist -> AuthorizationApplicationError.ClientNotExist.left()
                    ResponseTypeInvalid -> AuthorizationApplicationError.ResponseTypeInvalid.left()
                    ScopeInvalid -> AuthorizationApplicationError.ScopeInvalid.left()
                    RedirectUriInvalid -> AuthorizationApplicationError.RedirectUriInvalid.left()
                }
            }, { it })

        if (request.user == null && request.promptSet.contains(Prompt.NONE)) {
            return LoginRequired.left()
        }

        val consentUrl =
            consentUrlGenerator.generate(
                clientId,
                responseType,
                redirectUri,
                scope,
                state,
            )
        if (request.user == null || request.promptSet.contains(Prompt.LOGIN) || request.promptSet.contains(Prompt.SELECT_ACCOUNT)) {
            return Response(loginUrlGenerator.generate(consentUrl)).right()
        }

        val consent = consentRepository.get(request.user.id, request.client.clientId)
        if (consent == null && request.promptSet.contains(Prompt.NONE)) {
            return ConsentRequired.left()
        }
        if (consent == null ||
            !consent.satisfies(request.requestScopes) ||
            request.promptSet.contains(Prompt.CONSENT)
        ) {
            return Response(consentUrl).right()
        }

        val authorizationCode =
            if (request.responseType.hasCode()) {
                AuthorizationCode.generate(
                    request.user.id,
                    clientId,
                    ConsentedScopes(request.requestScopes.value),
                    redirectUri,
                    authorizationCodeRepository,
                )
            } else {
                null
            }

        val accessToken =
            if (request.responseType.hasToken()) {
                accessTokenConverter.encode(AccessTokenPayload.generate(request))
            } else {
                null
            }

        val idToken =
            if (request.responseType.hasIdToken()) {
                idTokenConverter.encode(IdTokenPayload.generate(request))
            } else {
                null
            }

        val callbackUri =
            clientCallbackUrlGenerator.generate(
                redirectUri = redirectUri,
                code = authorizationCode?.code,
                accessToken = accessToken,
                idToken = idToken,
                state = state,
            )

        return Response(callbackUri).right()
    }

    data class Response(val redirectUri: String)
}
