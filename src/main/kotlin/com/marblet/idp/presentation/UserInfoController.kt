package com.marblet.idp.presentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.marblet.idp.application.GetUserInfoUseCase
import com.marblet.idp.configration.EndpointPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.USERINFO_PATH)
class UserInfoController(private val getUserInfoUseCase: GetUserInfoUseCase) {
    @GetMapping
    fun get(
        @RequestParam(name = "access_token") accessToken: String,
    ) = getUserInfoUseCase.run(accessToken).fold(
        { throw UserInfoException(it) },
        { it },
    )

    data class UserInfoException(val error: GetUserInfoUseCase.Error) : Exception()

    data class ErrorResponse(
        val error: String,
        @JsonProperty("error_description")
        val errorDescription: String?,
    )

    @ExceptionHandler(UserInfoException::class)
    fun handleUserInfoException(exception: UserInfoException): ResponseEntity<ErrorResponse> {
        val error = exception.error
        return ResponseEntity.badRequest().body(
            ErrorResponse(error.error.error, error.description),
        )
    }
}
