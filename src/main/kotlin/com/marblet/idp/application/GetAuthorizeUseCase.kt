package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import org.springframework.stereotype.Service

@Service
class GetAuthorizeUseCase(
    private val authorizationRequestValidator: AuthorizationRequestValidator,
    private val consentUrlGenerator: ConsentUrlGenerator,
    private val loginUrlGenerator: LoginUrlGenerator,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        val request = authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope, loginCookie)
            .fold({ return it.left() }, { it })
        val consentUrl =
            consentUrlGenerator.generate(
                clientId,
                responseType,
                redirectUri,
                scope,
                state,
            )
        if (request.user != null) {
            return Response(consentUrl).right()
        }
        return Response(loginUrlGenerator.generate(consentUrl)).right()
    }

    data class Response(val redirectUri: String)
}
