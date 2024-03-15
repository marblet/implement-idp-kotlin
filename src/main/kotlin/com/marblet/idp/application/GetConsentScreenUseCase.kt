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
        prompt: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        val request =
            authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope, prompt, loginCookie)
                .fold({ return it.left() }, { it })
        if (request.user == null) {
            return UserNotAuthenticated.left()
        }
        return Response(request.client.name, request.requestScopes.toSpaceSeparatedString()).right()
    }

    data class Response(val clientName: String, val scope: String)
}
