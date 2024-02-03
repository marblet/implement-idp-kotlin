package com.marblet.idp.domain.model

sealed class ValidatedAuthorizationRequest(val client: Client, val responseType: ResponseType, val requestScope: Set<String>)

class OauthAuthorizationRequest(
    client: Client,
    responseType: ResponseType,
    requestScope: Set<String>,
) : ValidatedAuthorizationRequest(client, responseType, requestScope)
