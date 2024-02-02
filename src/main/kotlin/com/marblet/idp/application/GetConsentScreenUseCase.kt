package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.ClientNotExist
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotAuthenticated
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class GetConsentScreenUseCase(
    private val authorizationRequestValidator: AuthorizationRequestValidator,
    private val clientRepository: ClientRepository,
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
        authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope)
            .onLeft { return it.left() }
        val client = clientRepository.get(clientId) ?: return ClientNotExist.left()
        val requiredScope = scope ?: client.scopes.joinToString()
        return Response(client.name, requiredScope).right()
    }

    data class Response(val clientName: String, val scope: String)
}
