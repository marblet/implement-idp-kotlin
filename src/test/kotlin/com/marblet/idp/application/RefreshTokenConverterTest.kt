package com.marblet.idp.application

import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RefreshTokenPayload
import com.marblet.idp.domain.model.TokenScopes
import com.marblet.idp.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class RefreshTokenConverterTest {
    @Test
    fun converterCanEncodeAndDecodeRefreshToken() {
        val issuedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val refreshTokenPayload =
            RefreshTokenPayload(
                userId = UserId("user-id"),
                clientId = ClientId("client-id"),
                scopes = TokenScopes(setOf("a", "b", "c")),
                issuedAt = issuedAt,
                expiration = issuedAt.plusDays(28L),
            )
        val target = RefreshTokenConverter()

        val jwt = target.encode(refreshTokenPayload)
        val actual = target.decode(jwt)

        assertThat(actual).isEqualTo(refreshTokenPayload)
    }

    @Test
    fun decoderReturnsNullWhenTokenExpired() {
        val issuedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        val expiration = LocalDateTime.of(2023, 2, 1, 0, 0, 0)
        val refreshTokenPayload =
            RefreshTokenPayload(
                userId = UserId("user-id"),
                clientId = ClientId("client-id"),
                scopes = TokenScopes(setOf("a", "b", "c")),
                issuedAt = issuedAt,
                expiration = expiration,
            )
        val target = RefreshTokenConverter()

        val jwt = target.encode(refreshTokenPayload)
        val actual = target.decode(jwt)

        assertThat(actual).isNull()
    }

    @Test
    fun decoderReturnsNullWhenTokenInvalid() {
        val target = RefreshTokenConverter()

        val actual = target.decode("thisis.invalid.token")

        assertThat(actual).isNull()
    }
}
