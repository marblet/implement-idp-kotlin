package com.marblet.idp.presentation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonProperty
import com.marblet.idp.application.GetUserInfoUseCase
import com.marblet.idp.configration.EndpointPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.USERINFO_PATH)
class UserInfoController(private val getUserInfoUseCase: GetUserInfoUseCase) {
    @GetMapping
    fun get(
        @RequestHeader("Authorization") authorizationHeader: String,
    ) = getUserInfoUseCase.run(authorizationHeader).fold(
        { throw UserInfoException(it) },
        {
            ResponseBody(
                sub = it.sub,
                name = it.name,
                email = it.email,
                phoneNumber = it.phoneNumber,
                address = it.address,
            )
        },
    )

    @PostMapping
    fun post(
        @RequestHeader("Authorization") authorizationHeader: String,
    ) = getUserInfoUseCase.run(authorizationHeader).fold(
        { throw UserInfoException(it) },
        {
            ResponseBody(
                sub = it.sub,
                name = it.name,
                email = it.email,
                phoneNumber = it.phoneNumber,
                address = it.address,
            )
        },
    )

    @JsonInclude(NON_NULL)
    data class ResponseBody(
        val sub: String,
        val name: String?,
        val email: String?,
        @JsonProperty("phone_number") val phoneNumber: String?,
        val address: String?,
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
