package com.marblet.idp.configration

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@Configuration
class RsaKeyConfig(private val resourceLoader: ResourceLoader) {
    val rsaPrivateKey = initRsaPrivateKey()

    private fun initRsaPrivateKey(): RSAPrivateKey {
        val pem =
            resourceLoader.getResource("classpath:data/private.key")
                .file.useLines { it.toList() }
                .filter { !it.contains("-----BEGIN PRIVATE KEY-----") }
                .filter { !it.contains("-----END PRIVATE KEY-----") }
                .joinToString("")
        val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem))
        return KeyFactory.getInstance("RSA", BouncyCastleProvider()).generatePrivate(keySpec) as RSAPrivateKey
    }
}
