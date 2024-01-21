package com.marblet.idp.domain.model

class Client(val clientId: ClientId, val secret: String?, val redirectUris: Set<String>, val name: String, val scopes: Set<String>) {
    fun isValidRequest(
        requestScope: String?,
        requestRedirectUri: RedirectUri,
    ): Boolean {
        return true
    }
}
