package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.GetUserInfoUseCase.Error.InsufficientScope
import com.marblet.idp.application.GetUserInfoUseCase.Error.InvalidToken
import com.marblet.idp.application.GetUserInfoUseCase.Error.UserNotFound
import com.marblet.idp.domain.UserInfoError
import com.marblet.idp.domain.UserInfoError.INSUFFICIENT_SCOPE
import com.marblet.idp.domain.UserInfoError.INVALID_REQUEST
import com.marblet.idp.domain.UserInfoError.INVALID_TOKEN
import com.marblet.idp.domain.model.UserInfoRequestScopes
import com.marblet.idp.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetUserInfoUseCase(
    private val accessTokenConverter: AccessTokenConverter,
    private val userRepository: UserRepository,
) {
    fun run(accessToken: String): Either<Error, Response> {
        val accessTokenPayload = accessTokenConverter.decode(accessToken) ?: return InvalidToken.left()
        val userInfoRequestScopes = UserInfoRequestScopes.generate(accessTokenPayload.scopes) ?: return InsufficientScope.left()
        val user = userRepository.get(accessTokenPayload.userId.value) ?: return UserNotFound.left()
        return Response(
            sub = user.id.value,
        ).right()
    }

    data class Response(val sub: String)

    sealed class Error(val error: UserInfoError, val description: String) {
        data object InvalidToken : Error(INVALID_TOKEN, "invalid token")

        data object InsufficientScope : Error(INSUFFICIENT_SCOPE, "insufficient scope")

        data object UserNotFound : Error(INVALID_REQUEST, "user not found")
    }
}
