package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.AuthorizationError
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class GrantUseCase(
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        loginCookie: String?,
    ): Either<Error, Response> {
        // verify client
        val client = clientRepository.get(clientId) ?: return Error.ClientNotExist.left()

        // verify user
        if (loginCookie == null) {
            return Error.UserNotAuthenticated.left()
        }
        val user = userRepository.get(loginCookie) ?: return Error.UserNotFound.left()

        // verify request
        val requestedScope =
            if (scope?.isNotBlank() == true) {
                scope.split(" ").toSet()
            } else {
                client.scopes
            }
        if (!client.scopes.containsAll(requestedScope)) {
            return Error.ScopeInvalid.left()
        }
        if (!client.redirectUris.contains(redirectUri.value)) {
            return Error.RedirectUriInvalid.left()
        }
        if (responseType != "code") {
            return Error.ResponseTypeInvalid.left()
        }

        val authorizationCode = AuthorizationCode.generate(user.id, clientId, requestedScope)
        authorizationCodeRepository.insert(authorizationCode)
        return Response(redirectUri, authorizationCode.code, state).right()
    }

    sealed class Error(val error: AuthorizationError, val description: String) {
        data object UserNotAuthenticated : Error(AuthorizationError.INVALID_REQUEST, "user not logged in.")

        data object UserNotFound : Error(AuthorizationError.INVALID_REQUEST, "user not found.")

        data object ClientNotExist : Error(AuthorizationError.INVALID_REQUEST, "client_id is invalid.")

        data object ScopeInvalid : Error(AuthorizationError.INVALID_SCOPE, "scope is invalid.")

        data object RedirectUriInvalid : Error(AuthorizationError.INVALID_REQUEST, "redirect_uri is invalid.")

        data object ResponseTypeInvalid : Error(AuthorizationError.UNSUPPORTED_RESPONSE_TYPE, "response_type is invalid.")
    }

    class Response(redirectUri: RedirectUri, code: String, state: String?) {
        val redirectTo: String

        init {
            val builder =
                UriComponentsBuilder.fromUriString(redirectUri.value)
                    .queryParam("code", code)
            state?.let { builder.queryParam("state", it) }
            this.redirectTo = builder.build().toUriString()
        }
    }
}
