package com.marblet.idp.application

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.marblet.idp.application.error.AuthorizationApplicationError
import com.marblet.idp.application.error.AuthorizationApplicationError.LoginRequired
import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.ConsentedScopes
import com.marblet.idp.domain.model.Prompt
import com.marblet.idp.domain.model.RedirectUri
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import com.marblet.idp.domain.repository.ConsentRepository
import org.springframework.stereotype.Service

@Service
class GetAuthorizeUseCase(
    private val authorizationRequestValidator: AuthorizationRequestValidator,
    private val consentUrlGenerator: ConsentUrlGenerator,
    private val loginUrlGenerator: LoginUrlGenerator,
    private val clientCallbackUrlGenerator: ClientCallbackUrlGenerator,
    private val consentRepository: ConsentRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
) {
    fun run(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
        prompt: String?,
        loginCookie: String?,
    ): Either<AuthorizationApplicationError, Response> {
        val request =
            authorizationRequestValidator.validate(clientId, responseType, redirectUri, scope, prompt, loginCookie)
                .fold({ return it.left() }, { it })

        if (request.user == null && request.promptSet.contains(Prompt.NONE)) {
            return LoginRequired.left()
        }

        val consentUrl =
            consentUrlGenerator.generate(
                clientId,
                responseType,
                redirectUri,
                scope,
                state,
            )
        if (request.user == null || request.promptSet.contains(Prompt.LOGIN) || request.promptSet.contains(Prompt.SELECT_ACCOUNT)) {
            return Response(loginUrlGenerator.generate(consentUrl)).right()
        }

        val consent = consentRepository.get(request.user.id, request.client.clientId)
        if (consent == null ||
            !consent.satisfies(request.requestScopes) ||
            request.promptSet.contains(Prompt.CONSENT)
        ) {
            return Response(consentUrl).right()
        }

        val authorizationCode =
            AuthorizationCode.generate(
                request.user.id,
                clientId,
                ConsentedScopes(request.requestScopes.value),
                redirectUri,
                authorizationCodeRepository,
            )

        val callbackUri = clientCallbackUrlGenerator.generate(redirectUri, authorizationCode, state)

        return Response(callbackUri).right()
    }

    data class Response(val redirectUri: String)
}
