package com.marblet.idp.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorizationRequest(
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("response_type") val responseType: String,
    @JsonProperty("redirect_uri") val redirectUri: String,
    val scope: String,
    val state: String?,
    @JsonProperty("response_mode") val responseMode: String?,
    val nonce: String?,
    val display: String?,
    val prompt: String?,
    @JsonProperty("max_age") val maxAge: String?,
    @JsonProperty("ui_locales") val uiLocales: String?,
    @JsonProperty("id_token_hint") val idTokenHint: String?,
    @JsonProperty("login_hint") val loginHint: String?,
    @JsonProperty("acr_values") val acrValues: String?,
)
