package com.marblet.idp.domain.model

/**
 * TokenScopesは、認可コードに持たせるOauth2.0文脈のスコープを表す。
 * "openid"やUserInfoのClaimsとして使われるスコープは、このスコープから除外する。
 */
data class TokenScopes(val value: Set<String>) {
    companion object {
        fun fromSpaceSeparatedString(scope: String) = TokenScopes(scope.split(" ").toSet())
    }

    fun toSpaceSeparatedString() = value.joinToString(" ")
}
