package com.marblet.idp.domain.model

import java.time.LocalDateTime

data class AccessTokenPayload(
    val userId: UserId,
    val clientId: ClientId,
    val scopes: Set<String>,
    val issuedAt: LocalDateTime,
    val expiration: LocalDateTime,
) {
    companion object {
        const val EXPIRATION_SEC = 3600L

        fun generate(authorizationCode: AuthorizationCode): AccessTokenPayload? {
            if (authorizationCode.isExpired()) {
                return null
            }
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                authorizationCode.userId,
                authorizationCode.clientId,
                authorizationCode.scopes,
                issuedAt,
                issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }

        fun generate(
            refreshTokenPayload: RefreshTokenPayload,
            scopes: Set<String>?,
        ): AccessTokenPayload? {
            if (refreshTokenPayload.isExpired()) {
                return null
            }
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                refreshTokenPayload.userId,
                refreshTokenPayload.clientId,
                scopes ?: refreshTokenPayload.scopes,
                issuedAt,
                issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }
    }
}
