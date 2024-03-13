package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AuthorizationCodeTest {
    @Test
    fun generatedCodeLengthIs32() {
        val userId = UserId("user-1")
        val clientId = ClientId("client-1")
        val scopes = ConsentedScopes(setOf("a", "b"))
        val redirectUri = RedirectUri("test")

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri)

        assertThat(actual.code.length).isEqualTo(32)
    }

    @Test
    fun generatedCodeIsAlphanumeric() {
        val userId = UserId("user-1")
        val clientId = ClientId("client-1")
        val scopes = ConsentedScopes(setOf("a", "b"))
        val redirectUri = RedirectUri("test")

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri)

        assertThat(actual.code).matches("^[0-9a-zA-Z]+$")
    }

    @Test
    fun generatedCodeExpirationIsWithin10Minutes() {
        val userId = UserId("user-1")
        val clientId = ClientId("client-1")
        val scopes = ConsentedScopes(setOf("a", "b"))
        val redirectUri = RedirectUri("test")

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri)

        val currentTime = LocalDateTime.now()
        val expirationThreshold = LocalDateTime.now().plusMinutes(10)
        assertThat(actual.expiration).isAfter(currentTime)
        assertThat(actual.expiration).isBefore(expirationThreshold)
    }
}

class AuthorizationCodeScopesTest {
    @Test
    fun canGenerateFromSpaceSeparatedString() {
        val actual = AuthorizationCodeScopes.fromSpaceSeparatedString("a b c")

        assertThat(actual).isEqualTo(AuthorizationCodeScopes(setOf("a", "b", "c")))
    }

    @Test
    fun canGenerateSpaceSeparatedString() {
        val target = AuthorizationCodeScopes(setOf("a", "b", "c"))

        val actual = target.toSpaceSeparatedString()

        assertThat(actual).isEqualTo("a b c")
    }

    @Test
    fun returnTrueIfScopesHasOpenidScope() {
        val target = AuthorizationCodeScopes(setOf("openid", "email"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfNoOpenidScope() {
        val target = AuthorizationCodeScopes(setOf("a", "b"))

        val actual = target.hasOpenidScope()

        assertThat(actual).isFalse()
    }

    @Test
    fun returnTokenScopeIfAccessTokenScopesExist() {
        val target = AuthorizationCodeScopes(setOf("openid", "email", "a", "b"))

        val actual = target.toTokenScopes()

        assertThat(actual).isEqualTo(TokenScopes(setOf("a", "b")))
    }

    @Test
    fun returnNullIfAccessTokenScopesNotExist() {
        val target = AuthorizationCodeScopes(setOf("openid", "phone"))

        val actual = target.toTokenScopes()

        assertThat(actual).isNull()
    }
}
