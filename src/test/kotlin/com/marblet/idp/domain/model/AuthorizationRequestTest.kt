package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.Prompt.NONE
import com.marblet.idp.domain.model.ResponseType.CODE
import com.marblet.idp.domain.model.ResponseType.TOKEN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AuthorizationRequestTest {
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
        fun generateOAuth2Request() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                    prompt = null,
                )

            assertThat(actual.getOrNull()).usingRecursiveComparison().isEqualTo(
                OauthAuthorizationRequest(
                    client = client,
                    responseType = CODE,
                    requestScopes = RequestScopes(setOf("test")),
                    promptSet = PromptSet(setOf()),
                    user = user,
                ),
            )
        }

        @Test
        fun generateOIDCRequest() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "code",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email",
                    prompt = "none",
                )

            assertThat(actual.getOrNull()).usingRecursiveComparison().isEqualTo(
                OidcAuthorizationRequest(
                    client = client,
                    responseType = CODE,
                    requestScopes = RequestScopes(setOf("openid", "email")),
                    promptSet = PromptSet(setOf(NONE)),
                    user = user,
                ),
            )
        }
    }

    @Nested
    inner class OAuth2ImplicitFlowTest {
        @Test
        fun generateOAuth2Request() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "test",
                    prompt = null,
                )

            assertThat(actual.getOrNull()).usingRecursiveComparison().isEqualTo(
                OidcAuthorizationRequest(
                    client = client,
                    responseType = TOKEN,
                    requestScopes = RequestScopes(setOf("test")),
                    promptSet = PromptSet(setOf()),
                    user = user,
                ),
            )
        }

        @Test
        fun generateOAuth2RequestWithOpenidScope() {
            val actual =
                ValidatedAuthorizationRequest.create(
                    client = client,
                    user = user,
                    responseTypeInput = "token",
                    redirectUri = RedirectUri("http://example.com"),
                    scope = "openid email test",
                    prompt = null,
                )

            assertThat(actual.getOrNull()).usingRecursiveComparison().isEqualTo(
                OidcAuthorizationRequest(
                    client = client,
                    responseType = TOKEN,
                    requestScopes = RequestScopes(setOf("openid", "email", "test")),
                    promptSet = PromptSet(setOf()),
                    user = user,
                ),
            )
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
