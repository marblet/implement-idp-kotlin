package com.marblet.idp.infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.marblet.idp.configration.AppConfig
import com.marblet.idp.configration.RsaKeyConfig
import com.marblet.idp.domain.model.IdTokenPayload
import com.marblet.idp.domain.service.IdTokenConverter
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class IdTokenConverter(
    private val rsaKeyConfig: RsaKeyConfig,
    private val appConfig: AppConfig,
) : IdTokenConverter {
    private val algorithm = Algorithm.RSA256(null, rsaKeyConfig.rsaPrivateKey)

    override fun encode(payload: IdTokenPayload): String {
        return JWT.create()
            .withKeyId(rsaKeyConfig.kid)
            .withIssuer(appConfig.origin)
            .withSubject(payload.userId.value)
            .withAudience(payload.clientId.value)
            .withIssuedAt(Instant.ofEpochSecond(payload.issuedAt.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .withExpiresAt(Instant.ofEpochSecond(payload.expiration.atZone(ZoneId.systemDefault()).toEpochSecond()))
            .sign(algorithm)
    }
}
