package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AuthorizationError
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetConsentScreenUseCase(
    private val userRepository: UserRepository,
    private val clientRepository: ClientRepository,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        loginCookie: String?,
    ): Either<Error, Response> {
        if (loginCookie == null) {
            return Error.UserNotAuthenticated.left()
        }
        val client = clientRepository.get(clientId) ?: return Error.ClientNotExist.left()
        val requiredScope = scope ?: client.scopes.joinToString()
        return Response(client.name, requiredScope).right()
    }

    sealed class Error(val error: AuthorizationError, val description: String) {
        data object UserNotAuthenticated : Error(AuthorizationError.INVALID_REQUEST, "user not logged in.")

        data object ClientNotExist : Error(AuthorizationError.INVALID_REQUEST, "client_id is invalid.")
    }

    data class Response(val clientName: String, val scope: String)
}
