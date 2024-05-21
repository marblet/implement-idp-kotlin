package com.marblet.idp.domain.model

import arrow.core.Either
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
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                userId = authorizationCode.userId,
                clientId = authorizationCode.clientId,
                scopes = authorizationCode.scopes.toTokenScopes(),
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
                userId = refreshTokenPayload.userId,
                clientId = refreshTokenPayload.clientId,
                scopes = scopes.toTokenScopes(),
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }

        fun generate(validatedAuthorizationRequest: ValidatedAuthorizationRequest): AccessTokenPayload {
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                // TODO: remove '!!'
                userId = validatedAuthorizationRequest.user?.id!!,
                clientId = validatedAuthorizationRequest.client.clientId,
                scopes = validatedAuthorizationRequest.requestScopes.toTokenScopes(),
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }

        fun generate(validatedGrantRequest: ValidatedGrantRequest): AccessTokenPayload {
            val issuedAt = LocalDateTime.now()
            return AccessTokenPayload(
                userId = validatedGrantRequest.user.id,
                clientId = validatedGrantRequest.client.clientId,
                // TODO: remove '!!'
                scopes = validatedGrantRequest.consentedScopes.toTokenScopes(),
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
            )
        }
    }

    class CodeExpired
}
