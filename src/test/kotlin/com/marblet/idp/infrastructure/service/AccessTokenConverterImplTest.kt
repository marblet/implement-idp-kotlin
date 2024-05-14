package com.marblet.idp.infrastructure.service

import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.TokenScopes
import com.marblet.idp.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AccessTokenConverterImplTest {
    @Test
    fun converterCanEncodeAndDecodeAccessToken() {
        val issuedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        val accessTokenPayload =
            AccessTokenPayload(
                userId = UserId("user-id"),
                clientId = ClientId("client-id"),
                scopes = TokenScopes(setOf("a", "b", "c")),
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(3600L),
            )
        val target = AccessTokenConverterImpl()

        val jwt = target.encode(accessTokenPayload)
        val actual = target.decode(jwt)

        assertThat(actual).isEqualTo(accessTokenPayload)
    }

    @Test
    fun decoderReturnsNullWhenTokenExpired() {
        val issuedAt = LocalDateTime.of(2024, 1, 26, 22, 37, 0)
        val accessTokenPayload =
            AccessTokenPayload(
                userId = UserId("user-id"),
                clientId = ClientId("client-id"),
                scopes = TokenScopes(setOf("a", "b", "c")),
                issuedAt = issuedAt,
                expiration = issuedAt.plusSeconds(3600L),
            )
        val target = AccessTokenConverterImpl()

        val jwt = target.encode(accessTokenPayload)
        val actual = target.decode(jwt)

        assertThat(actual).isNull()
    }

    @Test
    fun decoderReturnsNullWhenTokenInvalid() {
        val target = AccessTokenConverterImpl()

        val actual = target.decode("thisis.invalid.token")

        assertThat(actual).isNull()
    }
}
