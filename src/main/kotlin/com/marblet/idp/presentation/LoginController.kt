package com.marblet.idp.presentation

import com.marblet.idp.config.EndpointPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.LOGIN_PATH)
class LoginController {
    @GetMapping
    fun getLoginScreen(
        @RequestParam(name = "done", required = true) done: String,
    ): ResponseEntity<Void> {
        TODO()
    }

    @PostMapping
    fun authenticateUser(
        @RequestParam(name = "done", required = true) done: String,
        @RequestBody requestBody: AuthenticateUserRequest,
    ): ResponseEntity<Void> {
        TODO()
    }

    data class AuthenticateUserRequest(
        val username: String,
        val password: String,
    )
}
