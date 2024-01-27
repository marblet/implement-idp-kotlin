package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AccessTokenPayload
import com.marblet.idp.domain.model.ClientId
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
) {
    fun run(
        authorizationHeader: String?,
        grantType: String,
        code: String,
        redirectUri: String,
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

        // verify request parameters
        if (grantType != "authorization_code") {
            return Error.InvalidGrantType.left()
        }

        val authorizationCode = authorizationCodeRepository.get(code) ?: return Error.InvalidAuthorizationCode.left()
        if (authorizationCode.redirectUri.value != redirectUri) {
            return Error.InvalidRedirectUri.left()
        }
        val payload = AccessTokenPayload.generate(authorizationCode) ?: return Error.AuthCodeExpired.left()
        val accessToken = accessTokenConverter.encode(payload)
        authorizationCodeRepository.delete(authorizationCode)
        // TODO issue refresh token
        return Response(accessToken, "bearer", AccessTokenPayload.EXPIRATION_SEC).right()
    }

    sealed class Error(val error: TokenError, val description: String) {
        data object InvalidClient : Error(TokenError.INVALID_CLIENT, "invalid client")

        data object InvalidGrantType : Error(TokenError.UNSUPPORTED_GRANT_TYPE, "grant_type must be authorization_code")

        data object InvalidRedirectUri : Error(TokenError.INVALID_REQUEST, "requested redirect_uri is not allowed")

        data object InvalidAuthorizationCode : Error(TokenError.INVALID_GRANT, "invalid authorization code")

        data object AuthCodeExpired : Error(TokenError.INVALID_GRANT, "authorization code has expired")
    }

    data class Response(
        val accessToken: String,
        val tokenType: String,
        val expiresIn: Long,
    )
}
