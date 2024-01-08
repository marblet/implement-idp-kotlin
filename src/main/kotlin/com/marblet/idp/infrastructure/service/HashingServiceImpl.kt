package com.marblet.idp.infrastructure.service

import com.marblet.idp.domain.service.HashingService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service

@Service
class HashingServiceImpl : HashingService {
    override fun hash(rawValue: String): String {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
            .encode(rawValue)
    }

    override fun matches(
        rawValue: String,
        hashedValue: String,
    ): Boolean {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
            .matches(rawValue, hashedValue)
    }
}
