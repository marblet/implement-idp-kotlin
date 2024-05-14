package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.RequestScopes
import com.marblet.idp.domain.model.TokenError
import com.marblet.idp.domain.repository.ConsentRepository
import com.marblet.idp.domain.service.AccessTokenConverter
import org.springframework.stereotype.Service

@Service
class RefreshAccessTokenUseCase(
    private val clientBasicAuthentication: ClientBasicAuthentication,
    private val refreshTokenConverter: RefreshTokenConverter,
    private val accessTokenConverter: AccessTokenConverter,
    private val consentRepository: ConsentRepository,
) {
    fun run(
        authorizationHeader: String?,
        refreshToken: String?,
        scope: String?,
    ): Either<Error, Response> {
        if (authorizationHeader == null) {
            return Error.AuthorizationHeaderEmpty.left()
        }
        if (refreshToken == null) {
            return Error.RefreshTokenEmpty.left()
        }
        val authenticatedClient = clientBasicAuthentication.authenticate(authorizationHeader) ?: return Error.ClientAuthFailed.left()
        val refreshTokenPayload = refreshTokenConverter.decode(refreshToken) ?: return Error.InvalidRefreshToken.left()

        // verify request parameters
        if (authenticatedClient.clientId != refreshTokenPayload.clientId) {
            return Error.InvalidClient.left()
        }
        val requestScopes = RequestScopes.generate(scope, refreshTokenPayload.scopes) ?: return Error.InvalidScope.left()

        // verify consent
        val consent = consentRepository.get(refreshTokenPayload.userId, refreshTokenPayload.clientId) ?: return Error.ConsentNotFound.left()
        if (!consent.satisfies(requestScopes)) {
            return Error.ConsentInvalid.left()
        }

        // issue access token
        val accessToken =
            AccessTokenPayload.generate(refreshTokenPayload, requestScopes)
                ?.let { accessTokenConverter.encode(it) }
                ?: return Error.RefreshTokenExpired.left()
        return Response(
            accessToken = accessToken,
            tokenType = "bearer",
            expiresIn = AccessTokenPayload.EXPIRATION_SEC,
        ).right()
    }

    sealed class Error(val error: TokenError, val description: String) {
        data object AuthorizationHeaderEmpty : Error(TokenError.INVALID_REQUEST, "authorization_header is required")

        data object RefreshTokenEmpty : Error(TokenError.INVALID_REQUEST, "refresh_token is required")

        data object ClientAuthFailed : Error(TokenError.INVALID_CLIENT, "client authentication failed")

        data object InvalidRefreshToken : Error(TokenError.INVALID_GRANT, "invalid refresh token")

        data object InvalidClient : Error(TokenError.INVALID_CLIENT, "invalid client")

        data object InvalidScope : Error(TokenError.INVALID_SCOPE, "invalid scope")

        data object RefreshTokenExpired : Error(TokenError.INVALID_REQUEST, "refresh_token has expired")

        data object ConsentNotFound : Error(TokenError.INVALID_GRANT, "consent not found")

        data object ConsentInvalid : Error(TokenError.INVALID_GRANT, "consent not sufficient")
    }

    data class Response(
        val accessToken: String,
        val tokenType: String,
        val expiresIn: Long,
    )
}
