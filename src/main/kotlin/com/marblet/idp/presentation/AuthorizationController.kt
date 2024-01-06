package com.marblet.idp.presentation

import com.marblet.idp.application.GetAuthorizeUseCase
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.presentation.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authorize")
class AuthorizationController(
    private val getAuthorizeUseCase: GetAuthorizeUseCase,
) {
    @GetMapping
    fun getAuthorize(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "redirect_uri") redirectUri: String,
        @RequestParam scope: String?,
        @RequestParam state: String?,
    ): ResponseEntity<Void> {
        return getAuthorizeUseCase.run(
            ClientId(clientId),
            responseType,
            RedirectUri(redirectUri),
            scope,
            state,
        ).fold(
            { throw GetAuthorizeException(it, state) },
            { ResponseEntity.status(HttpStatus.FOUND).header("Location", it.redirectUri).build() },
        )
    }

    data class GetAuthorizeException(val error: GetAuthorizeUseCase.Error, val state: String?) : Exception()

    @ExceptionHandler(value = [GetAuthorizeException::class])
    fun handleGetAuthorizeUseCaseException(exception: GetAuthorizeException): ResponseEntity<ErrorResponse> {
        return when (val error = exception.error) {
            is GetAuthorizeUseCase.Error.ClientNotExist ->
                ResponseEntity.badRequest().body(
                    ErrorResponse(error.error.error, error.description, exception.state),
                )
            is GetAuthorizeUseCase.Error.InvalidRedirectUri ->
                ResponseEntity.badRequest().body(
                    ErrorResponse(error.error.error, error.description, exception.state),
                )
        }
    }
}
