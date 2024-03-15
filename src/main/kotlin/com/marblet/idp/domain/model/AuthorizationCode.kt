package com.marblet.idp.domain.model

import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import java.time.LocalDateTime
import kotlin.random.Random

class AuthorizationCode(
    val code: String,
    val userId: UserId,
    val clientId: ClientId,
    val scopes: ConsentedScopes,
    val redirectUri: RedirectUri,
    val expiration: LocalDateTime,
) {
    companion object {
        private const val CODE_LENGTH = 32
        private const val EXPIRATION_MINUTES = 10L
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        fun generate(
            userId: UserId,
            clientId: ClientId,
            scopes: ConsentedScopes,
            redirectUri: RedirectUri,
            authorizationCodeRepository: AuthorizationCodeRepository,
        ): AuthorizationCode {
            val code =
                (1..CODE_LENGTH)
                    .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                    .joinToString("")
            val expiration = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
            val authorizationCode = AuthorizationCode(code, userId, clientId, scopes, redirectUri, expiration)
            authorizationCodeRepository.insert(authorizationCode)
            return authorizationCode
        }
    }

    fun isExpired(): Boolean {
        return expiration.isBefore(LocalDateTime.now())
    }
}
