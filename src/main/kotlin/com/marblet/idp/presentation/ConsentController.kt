package com.marblet.idp.presentation

import com.marblet.idp.application.GetConsentScreenUseCase
import com.marblet.idp.config.EndpointPath
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(EndpointPath.CONSENT_PATH)
class ConsentController(
    private val getConsentScreenUseCase: GetConsentScreenUseCase,
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
                model.addAttribute("scope", it.scope)
                "consent"
            },
        )
    }

    data class GetConsentScreenException(val error: GetConsentScreenUseCase.Error, val state: String?) : Exception()

    @PostMapping
    fun grant(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "redirect_uri") redirectUri: String?,
        @RequestParam scope: String?,
        @RequestParam state: String?,
    ): ResponseEntity<Void> {
        TODO()
    }
}
