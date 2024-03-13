package com.marblet.idp.domain.model

sealed class ValidatedGrantRequest(val client: Client, val responseType: ResponseType, val consentedScopes: ConsentedScopes)

class OauthGrantRequest(
    client: Client,
    responseType: ResponseType,
    consentedScopes: ConsentedScopes,
) : ValidatedGrantRequest(client, responseType, consentedScopes)

class OidcGrantRequest(
    client: Client,
    responseType: ResponseType,
    consentedScopes: ConsentedScopes,
) : ValidatedGrantRequest(client, responseType, consentedScopes)
