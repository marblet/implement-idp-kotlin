package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TokenScopesTest {
    @Test
    fun canGenerateFromSpaceSeparatedString() {
        val actual = TokenScopes.fromSpaceSeparatedString("a b c")

        assertThat(actual).isEqualTo(TokenScopes(setOf("a", "b", "c")))
    }

    @Test
    fun canGenerateSpaceSeparatedString() {
        val target = TokenScopes(setOf("a", "b", "c"))

        val actual = target.toSpaceSeparatedString()

        assertThat(actual).isEqualTo("a b c")
    }

    @Test
    fun returnTrueIfScopesHasOpenidScope() {
        val target = TokenScopes(setOf("openid", "email"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfNoOpenidScope() {
        val target = TokenScopes(setOf("a", "b"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isFalse()
    }
}
