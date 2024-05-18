package com.marblet.idp.application

import com.marblet.idp.domain.model.RedirectUri
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClientCallbackUrlGeneratorTest {
    val redirectUri = RedirectUri("https://example.com/hoge?key=value")

    @Test
    fun returnRedirectUriAppendingQueryParametersWhenOnlyCode() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = "test-code",
                accessToken = null,
                state = "state123",
            )

        assertThat(actual).isEqualTo("https://example.com/hoge?key=value&code=test-code&state=state123")
    }

    @Test
    fun returnRedirectUriAppendingFragmentsWhenTokenNotNull() {
        val target = ClientCallbackUrlGenerator()

        val actual =
            target.generate(
                redirectUri = redirectUri,
                code = "test-code",
                accessToken = "test-access-token",
                state = "state123",
            )

        assertThat(
            actual,
        ).isEqualTo("https://example.com/hoge?key=value#access_token=test-access-token&token_type=Bearer&code=test-code&state=state123")
    }
}
