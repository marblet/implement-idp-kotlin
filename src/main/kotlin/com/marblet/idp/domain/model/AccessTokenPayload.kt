package com.marblet.idp.domain.model

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
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

        fun generate(authorizationCode: AuthorizationCode): Either<CodeExpired, AccessTokenPayload?> {
            if (authorizationCode.isExpired()) {
                return CodeExpired().left()
            }
            val tokenScopes = authorizationCode.scopes.toTokenScopes() ?: return Right(null)
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                userId = authorizationCode.userId,
                clientId = authorizationCode.clientId,
                scopes = tokenScopes,
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
            ).right()
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

    class CodeExpired
}
