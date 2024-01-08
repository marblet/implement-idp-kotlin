package com.marblet.idp.domain.model

import com.marblet.idp.domain.service.HashingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HashedPasswordTest {
    class HashingServiceStub : HashingService {
        override fun hash(rawValue: String): String {
            return "$rawValue-hash"
        }

        override fun matches(
            rawValue: String,
            hashedValue: String,
        ): Boolean {
            return hash(rawValue) == hashedValue
        }
    }

    @Test
    fun generateHashedPasswordFromRawPassword() {
        val rawPassword = RawPassword("password")
        val hashingService = HashingServiceStub()

        val actual = HashedPassword.hashRawPassword(rawPassword, hashingService)

        val expect = HashedPassword("password-hash")
        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun returnTrueWhenPasswordsMatch() {
        val rawPassword = RawPassword("password")
        val target = HashedPassword("password-hash")
        val hashingService = HashingServiceStub()

        val actual = target.matches(rawPassword, hashingService)

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseWhenPasswordsMismatch() {
        val rawPassword = RawPassword("invalid-password")
        val target = HashedPassword("password-hash")
        val hashingService = HashingServiceStub()

        val actual = target.matches(rawPassword, hashingService)

        assertThat(actual).isFalse()
    }
}
