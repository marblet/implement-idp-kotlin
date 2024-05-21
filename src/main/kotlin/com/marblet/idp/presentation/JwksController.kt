package com.marblet.idp.presentation

import com.marblet.idp.configration.EndpointPath
import com.marblet.idp.configration.RsaKeyConfig
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.JWKS_PATH)
class JwksController(private val rsaKeyConfig: RsaKeyConfig) {
    @GetMapping
    fun getJwks() = ResponseEntity.ok().body(Response(rsaKeyConfig))

    class Response(rsaKeyConfig: RsaKeyConfig) {
        val keys =
            listOf(
                Key(
                    kty = "RSA",
                    kid = rsaKeyConfig.kid,
                    alg = "RS256",
                    use = "sig",
                    // TODO: Generate in RsaKeyConfig
                    n =
                        "gRtjwICtIC_4ae33Ks7S80n32PLFEC4UtBanBFE9Pjzcpp4XWDPgbbOkNC9BZ-Jkyq6aoP_UknfJPI-" +
                            "cIvE6IE96bPNGs6DcfZ73Cq2A9ZXTdiuuOiqMwhEgLKFVRUZZ50calENLGyi96-6lcDnwLehh-kEg7ARITmrBO0iAjFU",
                    e = "AQAB",
                ),
            )
    }

    data class Key(
        val kty: String,
        val kid: String,
        val alg: String,
        val use: String,
        val n: String,
        val e: String,
    )
}
