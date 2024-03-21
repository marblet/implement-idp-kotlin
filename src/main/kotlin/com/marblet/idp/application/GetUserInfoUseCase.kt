package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.GetUserInfoUseCase.Error.InsufficientScope
import com.marblet.idp.application.GetUserInfoUseCase.Error.InvalidToken
import com.marblet.idp.application.GetUserInfoUseCase.Error.UserNotFound
import com.marblet.idp.domain.model.UserInfoError
import com.marblet.idp.domain.model.UserInfoError.INSUFFICIENT_SCOPE
import com.marblet.idp.domain.model.UserInfoError.INVALID_REQUEST
import com.marblet.idp.domain.model.UserInfoError.INVALID_TOKEN
import com.marblet.idp.domain.model.UserInfoRequestScopes
import com.marblet.idp.domain.repository.UserInfoRepository
import org.springframework.stereotype.Service

@Service
class GetUserInfoUseCase(
    private val accessTokenConverter: AccessTokenConverter,
    private val userInfoRepository: UserInfoRepository,
) {
    fun run(accessToken: String): Either<Error, Response> {
        val accessTokenPayload = accessTokenConverter.decode(accessToken) ?: return InvalidToken.left()
        val userInfoRequestScopes = UserInfoRequestScopes.generate(accessTokenPayload.scopes) ?: return InsufficientScope.left()
        val userInfo = userInfoRepository.get(accessTokenPayload.userId) ?: return UserNotFound.left()
        val userInfoResponse = userInfo.toUserInfoResponse(userInfoRequestScopes)
        return Response(
            sub = userInfoResponse.sub,
            name = userInfoResponse.name,
            email = userInfoResponse.email,
            phoneNumber = userInfoResponse.phoneNumber,
            address = userInfoResponse.address,
        ).right()
    }

    data class Response(val sub: String, val name: String?, val email: String?, val phoneNumber: String?, val address: String?)

    sealed class Error(val error: UserInfoError, val description: String) {
        data object InvalidToken : Error(INVALID_TOKEN, "invalid token")

        data object InsufficientScope : Error(INSUFFICIENT_SCOPE, "insufficient scope")

        data object UserNotFound : Error(INVALID_REQUEST, "user not found")
    }
}
