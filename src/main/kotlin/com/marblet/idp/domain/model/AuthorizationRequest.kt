package com.marblet.idp.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right

sealed class ValidatedAuthorizationRequest(
    val client: Client,
    val responseType: ResponseType,
    val requestScopes: RequestScopes,
    val promptSet: PromptSet,
    val user: User?,
) {
    companion object {
        fun create(
            client: Client?,
            user: User?,
            responseTypeInput: String,
            redirectUri: RedirectUri,
            scope: String?,
            prompt: String?,
        ): Either<AuthorizationRequestCreateError, ValidatedAuthorizationRequest> {
            if (client == null) {
                return AuthorizationRequestCreateError.ClientNotExist.left()
            }
            val responseType = ResponseType.find(responseTypeInput) ?: return AuthorizationRequestCreateError.ResponseTypeInvalid.left()
            if (!client.redirectUris.contains(redirectUri.value)) {
                return AuthorizationRequestCreateError.RedirectUriInvalid.left()
            }
            val requestScopes = RequestScopes.generate(scope, client.scopes) ?: return AuthorizationRequestCreateError.ScopeInvalid.left()
            // ref. docs/protocol_and_flow.md
            if (responseType.hasToken() && requestScopes == RequestScopes(setOf(OpenidScope.OPENID.value))) {
                return AuthorizationRequestCreateError.ScopeInvalid.left()
            }
            val promptSet = PromptSet.from(prompt)
            return if (requestScopes.hasOpenidScope()) {
                OidcAuthorizationRequest(
                    client = client,
                    responseType = responseType,
                    requestScopes = requestScopes,
                    promptSet = promptSet,
                    user = user,
                ).right()
            } else {
                OauthAuthorizationRequest(
                    client = client,
                    responseType = responseType,
                    requestScopes = requestScopes,
                    promptSet = promptSet,
                    user = user,
                ).right()
            }
        }
    }
}

sealed class AuthorizationRequestCreateError {
    data object ClientNotExist : AuthorizationRequestCreateError()

    data object ResponseTypeInvalid : AuthorizationRequestCreateError()

    data object ScopeInvalid : AuthorizationRequestCreateError()

    data object RedirectUriInvalid : AuthorizationRequestCreateError()
}

class OauthAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
    promptSet: PromptSet,
    user: User?,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes, promptSet, user)

class OidcAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
    promptSet: PromptSet,
    user: User?,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes, promptSet, user)
