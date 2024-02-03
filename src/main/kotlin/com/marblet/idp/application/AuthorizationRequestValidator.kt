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
import com.marblet.idp.domain.model.OauthAuthorizationRequest
import com.marblet.idp.domain.model.OidcAuthorizationRequest
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.RequestScopes
import com.marblet.idp.domain.model.ResponseType.CODE
import com.marblet.idp.domain.model.ValidatedAuthorizationRequest
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
    ): Either<AuthorizationApplicationError, ValidatedAuthorizationRequest> {
        val client = clientRepository.get(clientId) ?: return ClientNotExist.left()
        if (responseType != "code") {
            return ResponseTypeInvalid.left()
        }
        if (!client.redirectUris.contains(redirectUri.value)) {
            return RedirectUriInvalid.left()
        }
        val requestScopes = RequestScopes.generate(scope, client.scopes) ?: return ScopeInvalid.left()
        return if (requestScopes.hasOpenidScope()) {
            OidcAuthorizationRequest(
                client = client,
                responseType = CODE,
                requestScopes = requestScopes,
            ).right()
        } else {
            OauthAuthorizationRequest(
                client = client,
                responseType = CODE,
                requestScopes = requestScopes,
            ).right()
        }
    }
}
