package com.marblet.idp.application

import com.marblet.idp.domain.model.RedirectUri
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class ClientCallbackUrlGenerator {
    fun generate(
        redirectUri: RedirectUri,
        code: String?,
        accessToken: String?,
        idToken: String?,
        state: String?,
    ): String {
        if (accessToken == null && idToken == null) {
            val builder =
                UriComponentsBuilder.fromUriString(redirectUri.value)
                    .queryParam("code", code)
            state?.let { builder.queryParam("state", it) }
            return builder.build().toUriString()
        }
        val fragment =
            listOfNotNull(
                accessToken?.let { "access_token=$it&token_type=Bearer" },
                idToken?.let { "id_token=$it" },
                code?.let { "code=$it" },
                state?.let { "state=$it" },
            )
                .joinToString("&")
        return UriComponentsBuilder.fromUriString(redirectUri.value)
            .fragment(fragment)
            .build().toUriString()
    }
}
