package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.UserNotAuthenticated
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ClientNotExist
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.RedirectUriInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ResponseTypeInvalid
import com.marblet.idp.domain.model.AuthorizationRequestCreateError.ScopeInvalid
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.model.ValidatedAuthorizationRequest
import com.marblet.idp.domain.repository.ClientRepository
import com.marblet.idp.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetConsentScreenUseCase(
    private val clientRepository: ClientRepository,
    private val userRepository: UserRepository,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        prompt: String?,
        nonce: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        val request =
            ValidatedAuthorizationRequest.create(
                client = clientRepository.get(clientId),
                user = loginCookie?.let { userRepository.get(loginCookie) },
                responseTypeInput = responseType,
                redirectUri = redirectUri,
                scope = scope,
                prompt = prompt,
                nonce = nonce,
            ).fold({
                return when (it) {
                    ClientNotExist -> AuthorizationApplicationError.ClientNotExist.left()
                    ResponseTypeInvalid -> AuthorizationApplicationError.ResponseTypeInvalid.left()
                    ScopeInvalid -> AuthorizationApplicationError.ScopeInvalid.left()
                    RedirectUriInvalid -> AuthorizationApplicationError.RedirectUriInvalid.left()
                }
            }, { it })
        if (request.user == null) {
            return UserNotAuthenticated.left()
        }
        return Response(request.client.name, request.requestScopes.toSpaceSeparatedString()).right()
    }

    data class Response(val clientName: String, val scope: String)
}
