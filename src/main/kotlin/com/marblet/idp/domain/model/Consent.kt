package com.marblet.idp.domain.model

data class Consent(
    val userId: UserId,
    val clientId: ClientId,
    val scopes: ConsentedScopes,
)

/**
 * ユーザ同意済みのスコープを表す。
 */
data class ConsentedScopes(val value: Set<String>) {
    companion object {
        fun fromSpaceSeparatedString(scope: String) = ConsentedScopes(scope.split(" ").toSet())
    }
}
