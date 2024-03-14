package com.marblet.idp.domain.model

sealed class ValidatedAuthorizationRequest(
    val client: Client,
    val responseType: ResponseType,
    val requestScopes: RequestScopes,
    val user: User?,
)

class OauthAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
    user: User?,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes, user)

class OidcAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
    user: User?,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes, user)
