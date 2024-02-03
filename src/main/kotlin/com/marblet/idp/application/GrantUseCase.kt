package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotAuthenticated
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotFound
import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class GrantUseCase(
    private val authorizationRequestValidator: AuthorizationRequestValidator,
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
    ): Either<AuthorizationApplicationError, Response> {
        // verify client
        val request =
            authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope)
                .fold({ return it.left() }, { it })

        // verify user
        if (loginCookie == null) {
            return UserNotAuthenticated.left()
        }
        val user = userRepository.get(loginCookie) ?: return UserNotFound.left()

        val authorizationCode = AuthorizationCode.generate(user.id, clientId, request.requestScope, redirectUri)
        authorizationCodeRepository.insert(authorizationCode)
        return Response(redirectUri, authorizationCode.code, state).right()
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
