package com.marblet.idp.presentation.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val error: String,
    @JsonProperty("error_description")
    val errorDescription: String?,
    val state: String?,
)
