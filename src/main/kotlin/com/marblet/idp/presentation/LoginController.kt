package com.marblet.idp.presentation

import com.marblet.idp.application.AuthenticateUseCase
import com.marblet.idp.config.EndpointPath
import com.marblet.idp.domain.model.RawPassword
import com.marblet.idp.domain.model.UnauthenticatedUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(EndpointPath.LOGIN_PATH)
class LoginController(private val authenticateUseCase: AuthenticateUseCase) {
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
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        return authenticateUseCase.authenticate(UnauthenticatedUser(user.username, RawPassword(user.password)))
            .fold(
                { throw AuthenticateUserException() },
                {
                    it.cookies.forEach { cookie -> response.addCookie(Cookie(cookie.key, cookie.value)) }
                    ResponseEntity.status(HttpStatus.FOUND).header("Location", done).build()
                },
            )
    }

    data class AuthenticateUserRequest(
        var username: String = "",
        var password: String = "",
    )

    class AuthenticateUserException : Exception()

    @ExceptionHandler(value = [AuthenticateUserException::class])
    fun handleAuthenticateUserException(exception: AuthenticateUserException): String {
        return "redirect:login"
    }
}
