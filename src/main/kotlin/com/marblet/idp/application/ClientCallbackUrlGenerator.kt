package com.marblet.idp.application

import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.RedirectUri
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class ClientCallbackUrlGenerator {
    fun generate(
        redirectUri: RedirectUri,
        authorizationCode: AuthorizationCode,
        state: String?,
    ): String {
        val builder =
            UriComponentsBuilder.fromUriString(redirectUri.value)
                .queryParam("code", authorizationCode.code)
        state?.let { builder.queryParam("state", it) }
        return builder.build().toUriString()
    }
}
