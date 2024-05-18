package com.marblet.idp.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class ValidatedGrantRequest private constructor(
    val client: Client,
    val responseType: ResponseType,
    val consentedScopes: ConsentedScopes,
    val user: User,
) {
    companion object {
        fun create(
            client: Client?,
            user: User?,
            responseTypeInput: String,
            redirectUri: RedirectUri,
            scope: String,
        ): Either<GrantRequestCreateError, ValidatedGrantRequest> {
            if (client == null) {
                return GrantRequestCreateError.ClientNotExist.left()
            }
            if (user == null) {
                return GrantRequestCreateError.UserNotFound.left()
            }
            val responseType = ResponseType.find(responseTypeInput) ?: return GrantRequestCreateError.ResponseTypeInvalid.left()
            if (!client.redirectUris.contains(redirectUri.value)) {
                return GrantRequestCreateError.RedirectUriInvalid.left()
            }
            val consentedScopes = ConsentedScopes.generate(scope, client.scopes) ?: return GrantRequestCreateError.ScopeInvalid.left()
            // ref. docs/protocol_and_flow.md
            if (responseType.hasToken() && consentedScopes == ConsentedScopes(setOf(OpenidScope.OPENID.value))) {
                return GrantRequestCreateError.ScopeInvalid.left()
            }
            return ValidatedGrantRequest(
                client = client,
                responseType = responseType,
                consentedScopes = consentedScopes,
                user = user,
            ).right()
        }
    }
}

sealed class GrantRequestCreateError {
    data object ClientNotExist : GrantRequestCreateError()

    data object ResponseTypeInvalid : GrantRequestCreateError()

    data object ScopeInvalid : GrantRequestCreateError()

    data object RedirectUriInvalid : GrantRequestCreateError()

    data object UserNotFound : GrantRequestCreateError()
}
