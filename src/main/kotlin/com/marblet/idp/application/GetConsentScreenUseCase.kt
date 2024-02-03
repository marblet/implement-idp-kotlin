package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotAuthenticated
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import org.springframework.stereotype.Service

@Service
class GetConsentScreenUseCase(
    private val authorizationRequestValidator: AuthorizationRequestValidator,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        if (loginCookie == null) {
            return UserNotAuthenticated.left()
        }
        val request =
            authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope)
                .fold({ return it.left() }, { it })
        return Response(request.client.name, request.requestScope.joinToString()).right()
    }

    data class Response(val clientName: String, val scope: String)
}
