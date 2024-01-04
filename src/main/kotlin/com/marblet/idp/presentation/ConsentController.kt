package com.marblet.idp.presentation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/consent")
class ConsentController {
    @GetMapping
    fun getConsentScreen(
        @RequestParam(name = "client_id") clientId: String,
        @RequestParam(name = "response_type") responseType: String,
        @RequestParam(name = "redirect_uri") redirectUri: String?,
        @RequestParam scope: String?,
        @RequestParam state: String?,
    ): ResponseEntity<Void> {
        TODO()
    }

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
