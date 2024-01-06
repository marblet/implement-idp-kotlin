package com.marblet.idp.application

import com.marblet.idp.config.EndpointPath
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.RedirectUri
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class ConsentUrlGenerator {
    fun generate(
        clientId: ClientId,
        responseType: String,
        redirectUri: RedirectUri,
        scope: String?,
        state: String?,
    ): String {
        val builder =
            UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080")
                .path(EndpointPath.CONSENT_PATH)
                .queryParam("client_id", clientId.value)
                .queryParam("response_type", responseType)
                .queryParam("redirect_uri", redirectUri.value)
        scope?.let { builder.queryParam("scope", it) }
        state?.let { builder.queryParam("state", it) }
        return builder.build().toUriString()
    }
}
