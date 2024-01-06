package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.domain.model.AuthorizationError
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class GetAuthorizeUseCase(
    private val clientRepository: ClientRepository,
    private val consentUrlGenerator: ConsentUrlGenerator,
    private val loginUrlGenerator: LoginUrlGenerator,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
    ): Either<Error, Response> {
        val client = clientRepository.get(clientId) ?: return Error.ClientNotExist.left()
        if (!client.redirectUris.contains(redirectUri.value)) {
            return Error.InvalidRedirectUri.left()
        }
        val consentUrl =
            consentUrlGenerator.generate(
                clientId,
                responseType,
                redirectUri,
                scope,
                state,
            )
        return Response(loginUrlGenerator.generate(consentUrl)).right()
    }

    sealed class Error(val error: AuthorizationError, val description: String) {
        data object ClientNotExist : Error(AuthorizationError.INVALID_REQUEST, "client_id is invalid.")

        data object InvalidRedirectUri : Error(AuthorizationError.INVALID_REQUEST, "redirect_uri is invalid.")
    }

    data class Response(val redirectUri: String)
}
