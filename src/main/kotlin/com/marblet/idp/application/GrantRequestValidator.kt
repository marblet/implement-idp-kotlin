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
import com.marblet.idp.domain.model.ConsentedScopes
import com.marblet.idp.domain.model.OauthGrantRequest
import com.marblet.idp.domain.model.OidcGrantRequest
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.ResponseType.CODE
import com.marblet.idp.domain.model.ValidatedGrantRequest
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class GrantRequestValidator(
    private val clientRepository: ClientRepository,
) {
    fun validate(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String,
    ): Either<AuthorizationApplicationError, ValidatedGrantRequest> {
        val client = clientRepository.get(clientId) ?: return ClientNotExist.left()
        if (responseType != "code") {
            return ResponseTypeInvalid.left()
        }
        if (!client.redirectUris.contains(redirectUri.value)) {
            return RedirectUriInvalid.left()
        }
        val consentedScopes = ConsentedScopes.generate(scope, client.scopes) ?: return ScopeInvalid.left()
        return if (consentedScopes.hasOpenidScope()) {
            OidcGrantRequest(
                client = client,
                responseType = CODE,
                consentedScopes = consentedScopes,
            ).right()
        } else {
            OauthGrantRequest(
                client = client,
                responseType = CODE,
                consentedScopes = consentedScopes,
            ).right()
        }
    }
}
