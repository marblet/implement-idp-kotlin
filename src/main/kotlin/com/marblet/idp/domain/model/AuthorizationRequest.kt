package com.marblet.idp.domain.model

sealed class ValidatedAuthorizationRequest(val client: Client, val responseType: ResponseType, val requestScopes: RequestScopes)

class OauthAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes)

class OidcAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScopes: RequestScopes,
) : ValidatedAuthorizationRequest(client, responseType, requestScopes)
