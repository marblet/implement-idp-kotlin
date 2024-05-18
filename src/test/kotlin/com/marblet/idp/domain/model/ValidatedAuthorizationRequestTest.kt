package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.Prompt.NONE
import com.marblet.idp.domain.model.ResponseType.CODE
import com.marblet.idp.domain.model.ResponseType.CODE_IDTOKEN_TOKEN
import com.marblet.idp.domain.model.ResponseType.IDTOKEN
import com.marblet.idp.domain.model.ResponseType.TOKEN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidatedAuthorizationRequestTest {
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
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    prompt = "none",
                )

            val request = actual.getOrNull()
            assertThat(request?.client).isEqualTo(client)
            assertThat(request?.responseType).isEqualTo(CODE)
            assertThat(request?.requestScopes).isEqualTo(RequestScopes(setOf("openid", "email")))
            assertThat(request?.promptSet).isEqualTo(PromptSet(setOf(NONE)))
            assertThat(request?.user).isEqualTo(user)
        }
    }

    @Nested
    inner class OAuth2ImplicitFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                    prompt = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(TOKEN)
            assertThat(request?.requestScopes).isEqualTo(RequestScopes(setOf("test")))
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }

    @Nested
    inner class OIDCImplicitFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "id_token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid",
                    prompt = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(IDTOKEN)
            assertThat(request?.requestScopes).isEqualTo(RequestScopes(setOf("openid")))
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "id_token token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "email",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }

    @Nested
    inner class OIDCHybridFlowTest {
        @Test
        fun generateRequest() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code id_token token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    prompt = null,
                )

            val request = actual.getOrNull()
            assertThat(request?.responseType).isEqualTo(CODE_IDTOKEN_TOKEN)
            assertThat(request?.requestScopes).isEqualTo(RequestScopes(setOf("openid", "email")))
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }

    @Nested
    inner class CommonErrorTest {
        @Test
        fun clientNullError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = null,
                    user = null,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ClientNotExist)
        }

        @Test
        fun invalidResponseTypeError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "invalid",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ResponseTypeInvalid)
        }

        @Test
        fun invalidRedirectUriError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://invalid-uri.com"),
                    scope = "openid email",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(RedirectUriInvalid)
        }

        @Test
        fun invalidScopeError() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email invalid-scope",
                    prompt = null,
                )

            assertThat(actual.leftOrNull()).isEqualTo(ScopeInvalid)
        }
    }
}
