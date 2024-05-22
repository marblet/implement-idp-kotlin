package com.marblet.idp.domain.model

import java.time.LocalDateTime

data class IdTokenPayload(
    val userId: UserId,
    val clientId: ClientId,
    val issuedAt: LocalDateTime,
    val expiration: LocalDateTime,
    val nonce: String?,
) {
    companion object {
        private const val EXPIRATION_SEC = 3600L

        fun generate(authorizationCode: AuthorizationCode): IdTokenPayload? {
            if (!authorizationCode.scopes.hasOpenidScope()) {
                return null
            }
            if (authorizationCode.isExpired()) {
                return null
            }
            val issuedAt = LocalDateTime.now()
            return IdTokenPayload(
                userId = authorizationCode.userId,
                clientId = authorizationCode.clientId,
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
                nonce = null,
            )
        }

        fun generate(validatedAuthorizationRequest: ValidatedAuthorizationRequest): IdTokenPayload {
            val issuedAt = LocalDateTime.now()
            return IdTokenPayload(
                // TODO: remove '!!'
                userId = validatedAuthorizationRequest.user?.id!!,
                clientId = validatedAuthorizationRequest.client.clientId,
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
                nonce = validatedAuthorizationRequest.nonce,
            )
        }

        fun generate(validatedGrantRequest: ValidatedGrantRequest): IdTokenPayload {
            val issuedAt = LocalDateTime.now()
            return IdTokenPayload(
                userId = validatedGrantRequest.user.id,
                clientId = validatedGrantRequest.client.clientId,
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(EXPIRATION_SEC),
                nonce = validatedGrantRequest.nonce,
            )
        }
    }
}
