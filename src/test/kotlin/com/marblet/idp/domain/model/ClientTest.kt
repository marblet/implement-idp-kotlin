package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClientTest {
    @Test
    fun canVerifyIfClientIsConfidential() {
        val target = Client(ClientId("1"), "secret", setOf(), "name", ClientScopes(setOf()))

        val actual = target.isConfidentialClient()

        assertThat(actual).isTrue()
    }

    @Test
    fun canVerifyIfClientIsNotConfidential() {
        val target = Client(ClientId("1"), null, setOf(), "name", ClientScopes(setOf()))

        val actual = target.isConfidentialClient()

        assertThat(actual).isFalse()
    }
}

class ClientScopesTest {
    @Test
    fun canGenerateFromSpaceSeparatedString() {
        val actual = ClientScopes.fromSpaceSeparatedString("a b c")

        assertThat(actual).isEqualTo(ClientScopes(setOf("a", "b", "c")))
    }

    @Test
    fun canGenerateSpaceSeparatedString() {
        val target = ClientScopes(setOf("a", "b", "c"))

        val actual = target.toSpaceSeparatedString()

        assertThat(actual).isEqualTo("a b c")
    }
}
