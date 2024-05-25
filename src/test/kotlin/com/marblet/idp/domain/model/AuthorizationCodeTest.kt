package com.marblet.idp.domain.model

import com.marblet.idp.domain.repository.AuthorizationCodeRepository
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
        val authorizationCodeRepository = DummyAuthorizationCodeRepository()

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri, null, authorizationCodeRepository)

        assertThat(actual.code.length).isEqualTo(32)
        authorizationCodeRepository.haveBeenCalledOnceWith(actual)
    }

    @Test
    fun generatedCodeIsAlphanumeric() {
        val userId = UserId("user-1")
        val clientId = ClientId("client-1")
        val scopes = ConsentedScopes(setOf("a", "b"))
        val redirectUri = RedirectUri("test")
        val authorizationCodeRepository = DummyAuthorizationCodeRepository()

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri, null, authorizationCodeRepository)

        assertThat(actual.code).matches("^[0-9a-zA-Z]+$")
        authorizationCodeRepository.haveBeenCalledOnceWith(actual)
    }

    @Test
    fun generatedCodeExpirationIsWithin10Minutes() {
        val userId = UserId("user-1")
        val clientId = ClientId("client-1")
        val scopes = ConsentedScopes(setOf("a", "b"))
        val redirectUri = RedirectUri("test")
        val authorizationCodeRepository = DummyAuthorizationCodeRepository()

        val actual = AuthorizationCode.generate(userId, clientId, scopes, redirectUri, null, authorizationCodeRepository)

        val currentTime = LocalDateTime.now()
        val expirationThreshold = LocalDateTime.now().plusMinutes(10)
        assertThat(actual.expiration).isAfter(currentTime)
        assertThat(actual.expiration).isBefore(expirationThreshold)
        authorizationCodeRepository.haveBeenCalledOnceWith(actual)
    }

    class DummyAuthorizationCodeRepository : AuthorizationCodeRepository {
        private val history = mutableListOf<AuthorizationCode>()

        override fun insert(authorizationCode: AuthorizationCode) {
            history.add(authorizationCode)
        }

        override fun get(code: String): AuthorizationCode? {
            TODO("Not yet implemented")
        }

        override fun delete(authorizationCode: AuthorizationCode) {
            TODO("Not yet implemented")
        }

        fun haveBeenCalledOnceWith(authorizationCode: AuthorizationCode) {
            assertThat(history).isEqualTo(listOf(authorizationCode))
        }
    }
}
