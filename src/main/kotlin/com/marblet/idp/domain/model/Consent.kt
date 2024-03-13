package com.marblet.idp.domain.model

data class Consent(
    val userId: UserId,
    val clientId: ClientId,
    val scopes: ConsentedScopes,
) {
    fun satisfies(requestScopes: RequestScopes): Boolean {
        return scopes.value.containsAll(requestScopes.value)
    }
}
