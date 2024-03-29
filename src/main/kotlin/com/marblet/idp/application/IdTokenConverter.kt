package com.marblet.idp.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.marblet.idp.configration.RsaKeyConfig
import com.marblet.idp.domain.model.IdTokenPayload
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class IdTokenConverter(
    rsaKeyConfig: RsaKeyConfig,
) {
    private val algorithm = Algorithm.RSA256(null, rsaKeyConfig.rsaPrivateKey)

    fun encode(payload: IdTokenPayload): String {
        return JWT.create()
            .withIssuer(payload.issuer)
            .withSubject(payload.userId.value)
            .withAudience(payload.clientId.value)
            .withIssuedAt(Instant.ofEpochSecond(payload.issuedAt.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .withExpiresAt(Instant.ofEpochSecond(payload.expiration.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .sign(algorithm)
    }
}
