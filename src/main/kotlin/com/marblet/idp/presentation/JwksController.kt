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
                        "nVCUpBZw_6tiEPYhZVm3IgfjprgkBjjL2L9-Y1ZLXQ8_37S5z_S26pqsyXayED_MSPYE1-mr8kBU54o-5F_" +
                            "XnUdTzW1FxkGs2kQYvHBvapHyias6AfrMPJ_tiBIr-t0VLm1ykrhmm1VRbjRk4bVj4hd2T8HxXco9vtCwoE" +
                            "pSiJCaRr6maiEjgOMqkp346gNZD7SGDXzC9HGjEHs5lKpXMgupllC1ygMH-kCt53oGr63oekSn2hEHO0TbV" +
                            "L8uj0wCHKFFH6vykaOLGf5LTs1K27K6plVs0mT3zpg6gTm4pJoHtGuXiVz5ZGg5EQORt8Q70RweCGXqynsJq6dnT2A-yw",
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
