package com.marblet.idp.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RefreshTokenPayload
import com.marblet.idp.domain.model.UserId
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class RefreshTokenConverter {
    companion object {
        // TODO load the secret from config
        private val algorithm = Algorithm.HMAC256("secret")
        private const val SCOPE_CLAIM_NAME = "sco"
    }

    fun encode(payload: RefreshTokenPayload): String {
        return JWT.create()
            .withSubject(payload.userId.value)
            .withIssuedAt(Instant.ofEpochSecond(payload.issuedAt.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .withExpiresAt(Instant.ofEpochSecond(payload.expiration.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .withAudience(payload.clientId.value)
            .withClaim(SCOPE_CLAIM_NAME, payload.scopes.joinToString(separator = " "))
            .sign(algorithm)
    }

    fun decode(refreshToken: String): RefreshTokenPayload? {
        val verifier = JWT.require(algorithm).build() // verify sign, iat, and exp
        try {
            val decodedJWT = verifier.verify(refreshToken)
            return RefreshTokenPayload(
                userId = UserId(decodedJWT.subject),
                clientId = ClientId(decodedJWT.audience[0]),
                scopes = decodedJWT.getClaim(SCOPE_CLAIM_NAME).asString().split(" ").toSet(),
                issuedAt = decodedJWT.issuedAtAsInstant.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                expiration = decodedJWT.expiresAtAsInstant.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            )
        } catch (exception: TokenExpiredException) {
            // TODO Use Either
            return null
        } catch (exception: JWTDecodeException) {
            return null
        } catch (exception: JWTVerificationException) {
            return null
        }
    }
}
