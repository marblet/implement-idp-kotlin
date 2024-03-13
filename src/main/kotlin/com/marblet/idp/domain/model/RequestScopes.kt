package com.marblet.idp.domain.model

/**
 * ClientリクエストのScopeを表す。
 */
data class RequestScopes(val value: Set<String>) {
    companion object {
        fun generate(
            scope: String?,
            clientScopes: ClientScopes,
        ): RequestScopes? {
            if (scope == null) {
                return RequestScopes(clientScopes.value)
            }
            val requestScopes = scope.split(" ").toSet()
            if (!clientScopes.value.containsAll(requestScopes)) {
                return null
            }
            return RequestScopes(requestScopes)
        }

        fun generate(
            scope: String?,
            tokenScopes: TokenScopes,
        ): RequestScopes? {
            if (scope == null) {
                return RequestScopes(tokenScopes.value)
            }
            val requestScopes = scope.split(" ").toSet()
            if (!tokenScopes.value.containsAll(requestScopes)) {
                return null
            }
            return RequestScopes(requestScopes)
        }
    }

    fun toSpaceSeparatedString() = value.joinToString(" ")

    fun toTokenScopes() = TokenScopes(value)

    fun hasOpenidScope() = value.contains(OpenidScope.OPENID.value)
}
