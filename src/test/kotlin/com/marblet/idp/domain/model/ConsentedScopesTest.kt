package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConsentedScopesTest {
    @Test
    fun canGenerateFromSpaceSeparatedString() {
        val actual = ConsentedScopes.fromSpaceSeparatedString("a b c")

        assertThat(actual).isEqualTo(ConsentedScopes(setOf("a", "b", "c")))
    }

    @Test
    fun canGenerateSpaceSeparatedString() {
        val target = ConsentedScopes(setOf("a", "b", "c"))

        val actual = target.toSpaceSeparatedString()

        assertThat(actual).isEqualTo("a b c")
    }

    @Test
    fun returnTrueIfScopesHasOpenidScope() {
        val target = ConsentedScopes(setOf("openid", "email"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfNoOpenidScope() {
        val target = ConsentedScopes(setOf("a", "b"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isFalse()
    }
}
