package com.marblet.idp.domain.model

import java.time.LocalDateTime

data class AccessTokenPayload(
    val userId: UserId,
    val clientId: ClientId,
    val scopes: TokenScopes,
    val issuedAt: LocalDateTime,
    val expiration: LocalDateTime,
) {
    companion object {
        const val EXPIRATION_SEC = 3600L

        fun generate(authorizationCode: AuthorizationCode): AccessTokenPayload? {
            if (authorizationCode.isExpired()) {
                return null
            }
            val tokenScopes = authorizationCode.scopes.toTokenScopes() ?: return null
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                userId = authorizationCode.userId,
                clientId = authorizationCode.clientId,
                scopes = tokenScopes,
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }

        fun generate(
            refreshTokenPayload: RefreshTokenPayload,
            scopes: RequestScopes,
        ): AccessTokenPayload? {
            if (refreshTokenPayload.isExpired()) {
                return null
            }
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                refreshTokenPayload.userId,
                refreshTokenPayload.clientId,
                scopes.toTokenScopes(),
                issuedAt,
                issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }
    }
}
