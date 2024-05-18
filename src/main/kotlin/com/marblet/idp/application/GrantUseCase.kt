package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotAuthenticated
import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.Consent
import com.marblet.idp.domain.model.GrantRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.GrantRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.UserNotFound
import com.marblet.idp.domain.model.IdTokenPayload
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.ValidatedGrantRequest
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.ConsentRepository
import com.marblet.idp.domain.repository.UserRepository
import com.marblet.idp.domain.service.AccessTokenConverter
import com.marblet.idp.domain.service.IdTokenConverter
import org.springframework.stereotype.Service

@Service
class GrantUseCase(
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val consentRepository: ConsentRepository,
    private val accessTokenConverter: AccessTokenConverter,
    private val idTokenConverter: IdTokenConverter,
    private val clientCallbackUrlGenerator: ClientCallbackUrlGenerator,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String,
        state: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        if (loginCookie == null) {
            return UserNotAuthenticated.left()
        }
        val request =
            ValidatedGrantRequest.create(
                client = clientRepository.get(clientId),
                user = userRepository.get(loginCookie),
                responseTypeInput = responseType,
                redirectUri = redirectUri,
                scope = scope,
            ).fold({
                return when (it) {
                    ClientNotExist -> AuthorizationApplicationError.ClientNotExist.left()
                    ResponseTypeInvalid -> AuthorizationApplicationError.ResponseTypeInvalid.left()
                    ScopeInvalid -> AuthorizationApplicationError.ScopeInvalid.left()
                    RedirectUriInvalid -> AuthorizationApplicationError.RedirectUriInvalid.left()
                    UserNotFound -> AuthorizationApplicationError.UserNotFound.left()
                }
            }, { it })

        consentRepository.upsert(Consent(request.user.id, clientId, request.consentedScopes))
        val authorizationCode =
            if (request.responseType.hasCode()) {
                AuthorizationCode.generate(
                    request.user.id,
                    clientId,
                    request.consentedScopes,
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

        return Response(
            redirectTo =
                clientCallbackUrlGenerator.generate(
                    redirectUri = redirectUri,
                    code = authorizationCode?.code,
                    accessToken = accessToken,
                    idToken = idToken,
                    state = state,
                ),
        ).right()
    }

    data class Response(val redirectTo: String)
}
