package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClientTest {
    @Test
    fun canVerifyIfClientIsConfidential() {
        val target = Client(ClientId("1"), "secret", setOf(), "name", setOf())

        val actual = target.isConfidentialClient()

        assertThat(actual).isTrue()
    }

    @Test
    fun canVerifyIfClientIsNotConfidential() {
        val target = Client(ClientId("1"), null, setOf(), "name", setOf())

        val actual = target.isConfidentialClient()

        assertThat(actual).isFalse()
    }
}
