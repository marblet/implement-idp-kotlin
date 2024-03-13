package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConsentTest {
    @Test
    fun returnTrueIfAllScopesAreConsented() {
        val target = Consent(UserId("user-id"), ClientId("client-id"), ConsentedScopes(setOf("a", "b", "c")))
        val requestScopes = RequestScopes(setOf("a", "b"))

        val actual = target.satisfies(requestScopes)

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfAllSomeScopesAreNotConsented() {
        val target = Consent(UserId("user-id"), ClientId("client-id"), ConsentedScopes(setOf("a", "b")))
        val requestScopes = RequestScopes(setOf("a", "c"))

        val actual = target.satisfies(requestScopes)

        assertThat(actual).isFalse()
    }
}
