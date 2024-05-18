package com.marblet.idp.application

import com.marblet.idp.domain.model.RedirectUri
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClientCallbackUrlGeneratorTest {
    private val redirectUri = RedirectUri("https://example.com/hoge?key=value")

    @Test
    fun returnRedirectUriAppendingQueryParametersWhenOnlyCode() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = "test-code",
                accessToken = null,
                idToken = null,
                state = "state123",
            )

        assertThat(actual).isEqualTo("https://example.com/hoge?key=value&code=test-code&state=state123")
    }

    @Test
    fun returnRedirectUriAppendingFragmentsWhenTokenExists() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = "test-code",
                accessToken = "test-access-token",
                idToken = null,
                state = "state123",
            )

        assertThat(
            actual,
        ).isEqualTo("https://example.com/hoge?key=value#access_token=test-access-token&token_type=Bearer&code=test-code&state=state123")
    }

    @Test
    fun returnRedirectUriAppendingFragmentsWhenIdTokenExists() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = "test-code",
                accessToken = null,
                idToken = "test-id-token",
                state = "state123",
            )

        assertThat(
            actual,
        ).isEqualTo("https://example.com/hoge?key=value#id_token=test-id-token&code=test-code&state=state123")
    }

    @Test
    fun returnRedirectUriAppendingFragmentsWhenTokenAndIdTokenExists() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = null,
                accessToken = "test-access-token",
                idToken = "test-id-token",
                state = null,
            )

        assertThat(
            actual,
        ).isEqualTo("https://example.com/hoge?key=value#access_token=test-access-token&token_type=Bearer&id_token=test-id-token")
    }
}
