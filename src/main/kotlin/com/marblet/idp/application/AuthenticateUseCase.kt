package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AuthorizationError
import com.marblet.idp.domain.model.UnauthenticatedUser
import com.marblet.idp.domain.repository.UserRepository
import com.marblet.idp.domain.service.HashingService
import org.springframework.stereotype.Service

@Service
class AuthenticateUseCase(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
) {
    fun authenticate(unauthenticatedUser: UnauthenticatedUser): Either<Error, Response> {
        val user = userRepository.findByUsername(unauthenticatedUser.username) ?: return Error.UserNotExist.left()
        if (!user.validate(unauthenticatedUser, hashingService)) {
            return Error.UserNotExist.left()
        }
        return Response(mapOf("login" to user.id)).right()
    }

    sealed class Error(val error: AuthorizationError, val description: String) {
        data object UserNotExist : Error(AuthorizationError.INVALID_REQUEST, "username and/or password are invalid.")
    }

    data class Response(val cookies: Map<String, String>)
}
