package com.marblet.idp.presentation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.marblet.idp.application.IssueTokenUseCase
import com.marblet.idp.application.RefreshAccessTokenUseCase
import com.marblet.idp.configration.EndpointPath
import com.marblet.idp.domain.model.GrantType
import com.marblet.idp.presentation.dto.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.TOKEN_PATH)
class TokenController(
    private val issueTokenUseCase: IssueTokenUseCase,
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase,
) {
    @PostMapping
    fun issueToken(
        @RequestHeader("Authorization") authorizationHeader: String?,
        @RequestBody requestBody: IssueTokenRequestBody,
    ): IssueTokenResponseBody {
        if (requestBody.grantType == GrantType.AUTHORIZATION_CODE.value) {
            return issueTokenUseCase.run(
                authorizationHeader,
                requestBody.code,
                requestBody.redirectUri,
                requestBody.clientId,
            ).fold(
                { throw IssueTokenException(it) },
                {
                    IssueTokenResponseBody(
                        accessToken = it.accessToken,
                        tokenType = it.tokenType,
                        expiresIn = it.expiresIn,
                        refreshToken = it.refreshToken,
                        idToken = it.idToken,
                    )
                },
            )
        }
        if (requestBody.grantType == GrantType.REFRESH_TOKEN.value) {
            return refreshAccessTokenUseCase.run(
                authorizationHeader,
                requestBody.refreshToken,
                requestBody.scope,
            ).fold(
                { throw RefreshAccessTokenException(it) },
                {
                    IssueTokenResponseBody(
                        accessToken = it.accessToken,
                        tokenType = it.tokenType,
                        expiresIn = it.expiresIn,
                    )
                },
            )
        }
        throw IssueTokenException(IssueTokenUseCase.Error.InvalidGrantType)
    }

    data class IssueTokenRequestBody(
        @JsonProperty("grant_type") val grantType: String,
        val code: String?,
        @JsonProperty("redirect_uri") val redirectUri: String?,
        @JsonProperty("client_id") val clientId: String?,
        @JsonProperty("refresh_token") val refreshToken: String?,
        @JsonProperty("scope") val scope: String?,
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class IssueTokenResponseBody(
        @JsonProperty("access_token") val accessToken: String?,
        @JsonProperty("token_type") val tokenType: String,
        @JsonProperty("expires_in") val expiresIn: Long,
        @JsonProperty("refresh_token") val refreshToken: String? = null,
        @JsonProperty("id_token") val idToken: String? = null,
    )

    // TODO: combine IssueTokenException and RefreshAccessTokenException
    data class IssueTokenException(val error: IssueTokenUseCase.Error) : Exception()

    @ExceptionHandler(IssueTokenException::class)
    fun handleIssueTokenException(exception: IssueTokenException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(
            ErrorResponse(error.error.error, error.description, null),
        )
    }

    data class RefreshAccessTokenException(val error: RefreshAccessTokenUseCase.Error) : Exception()

    @ExceptionHandler(RefreshAccessTokenException::class)
    fun handleRefreshAccessTokenException(exception: RefreshAccessTokenException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(
            ErrorResponse(error.error.error, error.description, null),
        )
    }
}
