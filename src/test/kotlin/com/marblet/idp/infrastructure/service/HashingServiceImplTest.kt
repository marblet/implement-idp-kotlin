package com.marblet.idp.infrastructure.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HashingServiceImplTest {
    @Test
    fun matchRawValueAndHashedValue() {
        val target = HashingServiceImpl()
        val rawValue = "password"
        val hashedValue = target.hash(rawValue)

        val actual = target.matches(rawValue, hashedValue)

        assertThat(actual).isTrue()
    }

    @Test
    fun mismatchRawValueAndHashedValue() {
        val target = HashingServiceImpl()
        val rawValue = "password"
        val hashedValue = target.hash("invalid-password")

        val actual = target.matches(rawValue, hashedValue)

        assertThat(actual).isFalse()
    }
}
