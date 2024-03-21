package com.marblet.idp.domain.model

/**
 * UserInfoエンドポイントへ要求されたScopeを表す。
 */
data class UserInfoRequestScopes(val value: Set<UserInfoScope>) {
    companion object {
        fun generate(tokenScopes: TokenScopes): UserInfoRequestScopes? {
            val scopes = tokenScopes.value.mapNotNull { scope -> UserInfoScope.entries.find { it.value == scope } }
            if (scopes.isEmpty()) {
                return null
            }
            return UserInfoRequestScopes(scopes.toSet())
        }
    }

    fun contains(scope: UserInfoScope): Boolean {
        return value.contains(scope)
    }
}
