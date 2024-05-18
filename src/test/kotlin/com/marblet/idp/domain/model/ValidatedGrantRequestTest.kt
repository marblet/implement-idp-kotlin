package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.GrantRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.GrantRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.UserNotFound
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidatedGrantRequestTest {
    val client =
        Client(
            clientId = ClientId("test-client-id"),
            secret = "test_secret",
            redirectUris = setOf("http://example.com"),
            name = "test client",
            scopes = ClientScopes(setOf("openid", "email", "test")),
        )

    val user =
        User(
            id = UserId("test-user-id"),
            username = "test user",
            password = HashedPassword("password"),
        )

    @Nested
    inner class AuthorizationCodeFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                )

            val request = actual.getOrNull()
            assertThat(request?.client).isEqualTo(client)
            assertThat(request?.responseType).isEqualTo(ResponseType.CODE)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid", "email")))
            assertThat(request?.user).isEqualTo(user)
        }
    }

    @Nested
    inner class OAuth2ImplicitFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                )

            val request = actual.getOrNull()
            assertThat(request?.client).isEqualTo(client)
            assertThat(request?.responseType).isEqualTo(ResponseType.TOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("test")))
            assertThat(request?.user).isEqualTo(user)
        }

        @Test
        fun generateRequestWithOpenidScope() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email test",
                )

            val request = actual.getOrNull()
            assertThat(request?.client).isEqualTo(client)
            assertThat(request?.responseType).isEqualTo(ResponseType.TOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid", "email", "test")))
            assertThat(request?.user).isEqualTo(user)
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid",
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }

    @Nested
    inner class CommonErrorTest {
        @Test
        fun createNullError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = null,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                )

            assertThat(actual.leftOrNull()).isEqualTo(ClientNotExist)
        }

        @Test
        fun userNullError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = null,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                )

            assertThat(actual.leftOrNull()).isEqualTo(UserNotFound)
        }

        @Test
        fun invalidResponseTypeError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "invalid",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                )

            assertThat(actual.leftOrNull()).isEqualTo(ResponseTypeInvalid)
        }

        @Test
        fun invalidRedirectUriError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://invalid-uri.com"),
                    scope = "openid email",
                )

            assertThat(actual.leftOrNull()).isEqualTo(RedirectUriInvalid)
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid invalid-scope",
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }
}
