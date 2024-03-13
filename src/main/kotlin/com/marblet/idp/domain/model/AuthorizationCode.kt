package com.marblet.idp.domain.model

import java.time.LocalDateTime
import kotlin.random.Random

class AuthorizationCode(
    val code: String,
    val userId: UserId,
    val clientId: ClientId,
    val scopes: AuthorizationCodeScopes,
    val redirectUri: RedirectUri,
    val expiration: LocalDateTime,
) {
    companion object {
        private const val CODE_LENGTH = 32
        private const val EXPIRATION_MINUTES = 10L
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        fun generate(
            userId: UserId,
            clientId: ClientId,
            scopes: ConsentedScopes,
            redirectUri: RedirectUri,
        ): AuthorizationCode {
            val code =
                (1..CODE_LENGTH)
                    .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                    .joinToString("")
            val expiration = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
            return AuthorizationCode(code, userId, clientId, scopes.toAuthorizationCodeScopes(), redirectUri, expiration)
        }
    }

    fun isExpired(): Boolean {
        return expiration.isBefore(LocalDateTime.now())
    }
}

/**
 * AuthorizationCodeScopesは、認可コードに持たせるOauth2.0文脈のスコープを表す。
 * "openid"やUserInfoのClaimsとして使われるスコープは、このスコープから除外する。
 */
data class AuthorizationCodeScopes(private val value: Set<String>) {
    companion object {
        fun fromSpaceSeparatedString(scope: String) = AuthorizationCodeScopes(scope.split(" ").toSet())
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
