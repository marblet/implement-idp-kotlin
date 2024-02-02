package com.marblet.idp.presentation

import com.marblet.idp.application.GetConsentScreenUseCase
import com.marblet.idp.application.GrantUseCase
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.config.EndpointPath
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.presentation.dto.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(EndpointPath.CONSENT_PATH)
class ConsentController(
    private val getConsentScreenUseCase: GetConsentScreenUseCase,
    private val grantUseCase: GrantUseCase,
) {
    @GetMapping
    fun getConsentScreen(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "redirect_uri") redirectUri: String,
        @RequestParam scope: String?,
        @RequestParam state: String?,
        @CookieValue("login") loginCookie: String?,
        model: Model,
    ): String {
        return getConsentScreenUseCase.run(
            ClientId(clientId),
            responseType,
            RedirectUri(redirectUri),
            scope,
            state,
            loginCookie,
        ).fold(
            { throw GetConsentScreenException(it, state) },
            {
                model.addAttribute("clientName", it.clientName)
                model.addAttribute("displayScope", it.scope)
                model.addAttribute("scope", scope)
                model.addAttribute("clientId", clientId)
                model.addAttribute("responseType", responseType)
                model.addAttribute("redirectUri", redirectUri)
                model.addAttribute("state", state)
                "consent"
            },
        )
    }

    data class GetConsentScreenException(val error: AuthorizationApplicationError, val state: String?) : Exception()

    @ExceptionHandler(value = [GetConsentScreenException::class])
    fun handleGetConsentScreenException(exception: GetConsentScreenException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(ErrorResponse(error.error.error, error.description, exception.state))
    }

    @PostMapping
    fun grant(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "redirect_uri") redirectUri: String,
        @RequestParam scope: String?,
        @RequestParam state: String?,
        @CookieValue("login") loginCookie: String?,
    ): String {
        return grantUseCase.run(
            ClientId(clientId),
            responseType,
            RedirectUri(redirectUri),
            scope,
            state,
            loginCookie,
        ).fold(
            { throw GrantException(it, state) },
            { "redirect:${it.redirectTo}" },
        )
    }

    data class GrantException(val error: AuthorizationApplicationError, val state: String?) : Exception()

    @ExceptionHandler(value = [GrantException::class])
    fun handleGrantException(exception: GrantException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(ErrorResponse(error.error.error, error.description, exception.state))
    }
}
