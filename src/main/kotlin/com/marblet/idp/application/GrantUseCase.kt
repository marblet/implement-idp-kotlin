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
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.ValidatedGrantRequest
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.ConsentRepository
import com.marblet.idp.domain.repository.UserRepository
import com.marblet.idp.domain.service.AccessTokenConverter
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class GrantUseCase(
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val consentRepository: ConsentRepository,
    private val accessTokenConverter: AccessTokenConverter,
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
                val accessTokenPayload = AccessTokenPayload.generate(request)
                accessTokenConverter.encode(accessTokenPayload)
            } else {
                null
            }

        return Response(redirectUri, authorizationCode?.code, accessToken, state).right()
    }

    class Response(redirectUri: RedirectUri, code: String?, accessToken: String?, state: String?) {
        val redirectTo: String

        init {
            this.redirectTo =
                if (accessToken == null) {
                    val builder =
                        UriComponentsBuilder.fromUriString(redirectUri.value)
                            .queryParam("code", code)
                    state?.let { builder.queryParam("state", it) }
                    builder.build().toUriString()
                } else {
                    val fragment =
                        listOfNotNull(
                            accessToken?.let { "access_token=$accessToken&token_type=Bearer" },
                            code?.let { "code=$it" },
                            state?.let { "state=$it" },
                        )
                            .joinToString("&")
                    UriComponentsBuilder.fromUriString(redirectUri.value)
                        .fragment(fragment)
                        .build().toUriString()
                }
        }
    }
}
