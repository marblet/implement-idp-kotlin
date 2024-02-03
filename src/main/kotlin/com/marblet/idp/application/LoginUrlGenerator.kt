package com.marblet.idp.application

import com.marblet.idp.configration.EndpointPath
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class LoginUrlGenerator {
    fun generate(done: String?): String {
        val builder =
            UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080")
                .path(EndpointPath.LOGIN_PATH)
        done?.let { builder.queryParam("done", it) }
        return builder.encode().build().toUriString()
    }
}
