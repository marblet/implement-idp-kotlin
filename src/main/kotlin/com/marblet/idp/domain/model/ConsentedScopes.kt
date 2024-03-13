package com.marblet.idp.domain.model

/**
 * ユーザ同意済みのスコープを表す。
 */
data class ConsentedScopes(private val value: Set<String>) {
    companion object {
        fun generate(
            scope: String,
            clientScopes: ClientScopes,
        ): ConsentedScopes? {
            val requestScopes = scope.split(" ").toSet()
            if (!clientScopes.value.containsAll(requestScopes)) {
                return null
            }
            return ConsentedScopes(requestScopes)
        }

        fun fromSpaceSeparatedString(scope: String) = ConsentedScopes(scope.split(" ").toSet())
    }

    fun toSpaceSeparatedString() = value.joinToString(" ")

    fun toTokenScopes(): TokenScopes? {
        // remove UserInfo scopes and openid scope
        val scopes = value - (UserInfoScope.entries.map { it.value }.toSet() + OpenidScope.entries.map { it.value }.toSet())
        if (scopes.isEmpty()) {
            return null
        }
        return TokenScopes(scopes)
    }

    fun hasOpenidScope() = value.contains(OpenidScope.OPENID.value)
}
