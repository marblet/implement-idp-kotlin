package com.marblet.idp.presentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.marblet.idp.configration.AppConfig
import com.marblet.idp.configration.EndpointPath
import com.marblet.idp.domain.model.ResponseType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointPath.OPENID_CONFIGURATION_PATH)
class OpenidConfigurationController(private val appConfig: AppConfig) {
    @GetMapping
    fun getOpenidConfiguration() = ResponseEntity.status(HttpStatus.OK).body(Response(appConfig))

    class Response(appConfig: AppConfig) {
        val issuer: String = appConfig.origin

        @JsonProperty("authorization_endpoint")
        val authorizationEndpoint: String = "${appConfig.origin}/${EndpointPath.AUTHORIZE_PATH}"

        @JsonProperty("token_endpoint")
        val tokenEndpoint: String = "${appConfig.origin}/${EndpointPath.TOKEN_PATH}"

        @JsonProperty("userinfo_endpoint")
        val userinfoEndpoint: String = "${appConfig.origin}/${EndpointPath.USERINFO_PATH}"

        @JsonProperty("jwks_uri")
        val jwksUri: String = "${appConfig.origin}/${EndpointPath.JWKS_PATH}"

        @JsonProperty("response_types_supported")
        val responseTypesSupported: List<String> = ResponseType.entries.map { it.value }
    }
}
