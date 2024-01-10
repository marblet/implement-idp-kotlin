package com.marblet.idp.presentation

import com.marblet.idp.config.EndpointPath
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(EndpointPath.LOGIN_PATH)
class LoginController {
    @GetMapping
    fun getLoginScreen(
        @RequestParam(name = "done", required = true) done: String,
        model: Model,
    ): String {
        model.addAttribute("user", AuthenticateUserRequest())
        model.addAttribute("done", done)
        return "login"
    }

    @PostMapping
    fun authenticateUser(
        @RequestParam(name = "done", required = true) done: String,
        @ModelAttribute user: AuthenticateUserRequest,
    ): ResponseEntity<Void> {
        print(user)
        return ResponseEntity.status(200).build()
    }

    data class AuthenticateUserRequest(
        var username: String = "",
        var password: String = "",
    )
}
