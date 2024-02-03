package com.marblet.idp.configration

import org.springframework.context.annotation.Configuration
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
class RsaKeyConfig(
    var rsaPublicKey: RSAPublicKey,
    var rsaPrivateKey: RSAPrivateKey,
)
