package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.IssueTokenUseCase.Error.AuthCodeExpired
import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RefreshTokenPayload
import com.marblet.idp.domain.model.TokenError
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class IssueTokenUseCase(
    private val clientBasicAuthentication: ClientBasicAuthentication,
    private val clientRepository: ClientRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val accessTokenConverter: AccessTokenConverter,
    private val refreshTokenConverter: RefreshTokenConverter,
    private val idTokenGenerator: IdTokenGenerator,
) {
    fun run(
        authorizationHeader: String?,
        code: String?,
        redirectUri: String?,
        clientId: String?,
    ): Either<Error, Response> {
        // Client Authentication
        val client =
            if (authorizationHeader != null) {
                clientBasicAuthentication.authenticate(authorizationHeader).takeIf {
                    // if both authorizationHeader and clientId in the requestBody exist,
                    // verify the authenticated client and clientId are the same
                    clientId == null || it?.clientId?.value == clientId
                }
            } else {
                clientId?.let { clientRepository.get(ClientId(clientId)) }?.takeIf {
                    it.secret == null
                }
            }
        if (client == null) {
            return Error.InvalidClient.left()
        }

        // verify request params
        if (code == null) {
            return Error.CodeEmpty.left()
        }
        if (redirectUri == null) {
            return Error.RedirectUriEmpty.left()
        }

        // issue access token
        val authorizationCode = authorizationCodeRepository.get(code) ?: return Error.InvalidAuthorizationCode.left()
        if (authorizationCode.redirectUri.value != redirectUri) {
            return Error.InvalidRedirectUri.left()
        }
        val accessTokenPayload =
            AccessTokenPayload.generate(authorizationCode)
                .fold({ return AuthCodeExpired.left() }, { it })
        val accessToken = accessTokenPayload?.let { accessTokenConverter.encode(it) }

        // issue refresh token
        val refreshToken =
            if (client.isConfidentialClient()) {
                RefreshTokenPayload.generate(authorizationCode)?.let { refreshTokenConverter.encode(it) }
            } else {
                null
            }

        // issue IDToken
        val idToken = idTokenGenerator.generate(authorizationCode)

        authorizationCodeRepository.delete(authorizationCode)

        return Response(
            accessToken = accessToken,
            tokenType = "bearer",
            expiresIn = AccessTokenPayload.EXPIRATION_SEC,
            refreshToken = refreshToken,
            idToken = idToken,
        ).right()
    }

    sealed class Error(val error: TokenError, val description: String) {
        data object InvalidClient : Error(TokenError.INVALID_CLIENT, "invalid client")

        data object CodeEmpty : Error(TokenError.INVALID_REQUEST, "code is required")

        data object RedirectUriEmpty : Error(TokenError.INVALID_REQUEST, "redirect_uri is required")

        data object InvalidRedirectUri : Error(TokenError.INVALID_REQUEST, "requested redirect_uri is not allowed")

        data object InvalidAuthorizationCode : Error(TokenError.INVALID_GRANT, "invalid authorization code")

        data object AuthCodeExpired : Error(TokenError.INVALID_GRANT, "authorization code has expired")

        data object InvalidGrantType : Error(TokenError.UNSUPPORTED_GRANT_TYPE, "grant_type is invalid")
    }

    data class Response(
        val accessToken: String?,
        val tokenType: String,
        val expiresIn: Long,
        val refreshToken: String?,
        val idToken: String?,
    )
}
