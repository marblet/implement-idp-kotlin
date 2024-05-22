package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.GrantRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.GrantRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.GrantRequestCreateError.UserNotFound
import com.marblet.idp.domain.model.ResponseType.CODE_IDTOKEN_TOKEN
import com.marblet.idp.domain.model.ResponseType.IDTOKEN
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
                    nonce = "nonce123",
                )

            val request = actual.getOrNull()
            assertThat(request?.client).isEqualTo(client)
            assertThat(request?.responseType).isEqualTo(ResponseType.CODE)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid", "email")))
            assertThat(request?.user).isEqualTo(user)
            assertThat(request?.nonce).isEqualTo("nonce123")
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
                    nonce = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(ResponseType.TOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("test")))
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
                    nonce = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(ResponseType.TOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid", "email", "test")))
        }

        @Test
        fun generateRequestOfOpenidScope() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid",
                    nonce = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid")))
        }
    }

    @Nested
    inner class OIDCImplicitFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "id_token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid",
                    nonce = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(IDTOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid")))
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "id_token token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "email",
                    nonce = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }

    @Nested
    inner class OIDCHybridFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code id_token token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    nonce = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(CODE_IDTOKEN_TOKEN)
            assertThat(request?.consentedScopes).isEqualTo(ConsentedScopes(setOf("openid", "email")))
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedGrantRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                    nonce = null,
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
                    nonce = null,
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
                    nonce = null,
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
                    nonce = null,
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
                    nonce = null,
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
                    nonce = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }
}
