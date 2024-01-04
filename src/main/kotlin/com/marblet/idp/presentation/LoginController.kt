package com.marblet.idp.presentation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController {
    @GetMapping
    fun getLoginScreen(
        @RequestParam(name = "redirect_uri", required = true) redirectUri: String,
    ): ResponseEntity<Void> {
        TODO()
    }

    @PostMapping
    fun authenticateUser(
        @RequestParam(name = "redirect_uri", required = true) redirectUri: String,
        @RequestBody requestBody: AuthenticateUserRequest,
    ): ResponseEntity<Void> {
        TODO()
    }

    data class AuthenticateUserRequest(
        val username: String,
        val password: String,
    )
}
