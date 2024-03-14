package com.marblet.idp.domain.model

sealed class ValidatedAuthorizationRequest(
    val client: Client,
    val responseType: ResponseType,
    val requestScopes: RequestScopes,
    val promptSet: PromptSet,
    val user: User?,
)

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
