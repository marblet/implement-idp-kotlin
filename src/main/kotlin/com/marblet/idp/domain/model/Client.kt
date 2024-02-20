package com.marblet.idp.domain.model

class Client(val clientId: ClientId, val secret: String?, val redirectUris: Set<String>, val name: String, val scopes: ClientScopes) {
    fun isConfidentialClient(): Boolean {
        return secret != null
    }
}

data class ClientId(val value: String)

/**
 * Clientにあらかじめ登録されたスコープを表す。
 */
data class ClientScopes(val value: Set<String>) {
    companion object {
        fun fromSpaceSeparatedString(scope: String) = ClientScopes(scope.split(" ").toSet())
    }

    fun toSpaceSeparatedString() = value.joinToString(" ")
}
