package com.marblet.idp.domain.model

import java.time.LocalDateTime

data class RefreshTokenPayload(
    val userId: UserId,
    val clientId: ClientId,
    val scopes: TokenScopes,
    val issuedAt: LocalDateTime,
    val expiration: LocalDateTime,
) {
    companion object {
        private const val EXPIRATION_DAYS = 28L

        fun generate(authorizationCode: AuthorizationCode): RefreshTokenPayload? {
            if (authorizationCode.isExpired()) {
                return null
            }
            val tokenScopes = authorizationCode.scopes.toTokenScopes() ?: return null
            val issuedAt = LocalDateTime.now()
            return RefreshTokenPayload(
                authorizationCode.userId,
                authorizationCode.clientId,
                tokenScopes,
                issuedAt,
                issuedAt.plusDays(EXPIRATION_DAYS),
            )
        }
    }

    fun isExpired(): Boolean {
        return expiration.isBefore(LocalDateTime.now())
    }
}
