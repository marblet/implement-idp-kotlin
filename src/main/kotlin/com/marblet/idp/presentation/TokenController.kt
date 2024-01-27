package com.marblet.idp.presentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.marblet.idp.application.IssueTokenUseCase
import com.marblet.idp.config.EndpointPath
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
class TokenController(private val issueTokenUseCase: IssueTokenUseCase) {
    @PostMapping
    fun issueToken(
        @RequestHeader("Authorization") authorizationHeader: String?,
        @RequestBody requestBody: IssueTokenRequestBody,
    ): IssueTokenResponseBody {
        return issueTokenUseCase.run(
            authorizationHeader,
            requestBody.grantType,
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
                )
            },
        )
    }

    data class IssueTokenRequestBody(
        @JsonProperty("grant_type") val grantType: String,
        val code: String,
        @JsonProperty("redirect_uri") val redirectUri: String,
        @JsonProperty("client_id") val clientId: String?,
    )

    data class IssueTokenResponseBody(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("token_type") val tokenType: String,
        @JsonProperty("expires_in") val expiresIn: Long,
    )

    data class IssueTokenException(val error: IssueTokenUseCase.Error) : Exception()

    @ExceptionHandler(IssueTokenException::class)
    fun handleIssueTokenException(exception: IssueTokenException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(
            ErrorResponse(error.error.error, error.description, null),
        )
    }
}
