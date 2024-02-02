package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.ClientNotExist
import com.marblet.idp.application.error.AuthorizationApplicationError.RedirectUriInvalid
import com.marblet.idp.application.error.AuthorizationApplicationError.ResponseTypeInvalid
import com.marblet.idp.application.error.AuthorizationApplicationError.ScopeInvalid
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class AuthorizationRequestValidator(
    private val clientRepository: ClientRepository,
) {
    fun validate(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
    ): Either<AuthorizationApplicationError, Unit> {
        val client = clientRepository.get(clientId) ?: return ClientNotExist.left()
        if (responseType != "code") {
            return ResponseTypeInvalid.left()
        }
        if (!client.redirectUris.contains(redirectUri.value)) {
            return RedirectUriInvalid.left()
        }
        val requestedScope =
            if (scope?.isNotBlank() == true) {
                scope.split(" ").toSet()
            } else {
                client.scopes
            }
        if (!client.scopes.containsAll(requestedScope)) {
            return ScopeInvalid.left()
        }
        return Unit.right()
    }
}