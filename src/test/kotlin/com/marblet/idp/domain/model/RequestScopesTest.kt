package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RequestScopesTest {
    @Test
    fun canGenerateFromClientScopesWhenScopeNull() {
        val actual = RequestScopes.generate(null, ClientScopes(setOf("a", "b")))

        assertThat(actual).isEqualTo(RequestScopes(setOf("a", "b")))
    }

    @Test
    fun canGenerateFromClientScopesIfRequestValid() {
        val actual = RequestScopes.generate("a b", ClientScopes(setOf("a", "b", "c")))

        assertThat(actual).isEqualTo(RequestScopes(setOf("a", "b")))
    }

    @Test
    fun generateFromClientScopesReturnNullIfRequestInvalid() {
        val actual = RequestScopes.generate("a d", ClientScopes(setOf("a", "b", "c")))

        assertThat(actual).isNull()
    }

    @Test
    fun canGenerateFromTokenScopesWhenScopeNull() {
        val actual = RequestScopes.generate(null, TokenScopes(setOf("a", "b")))

        assertThat(actual).isEqualTo(RequestScopes(setOf("a", "b")))
    }

    @Test
    fun canGenerateFromTokenScopesIfRequestValid() {
        val actual = RequestScopes.generate("a b", TokenScopes(setOf("a", "b", "c")))

        assertThat(actual).isEqualTo(RequestScopes(setOf("a", "b")))
    }

    @Test
    fun generateFromTokenScopesReturnNullIfRequestInvalid() {
        val actual = RequestScopes.generate("a d", TokenScopes(setOf("a", "b", "c")))

        assertThat(actual).isNull()
    }
}
