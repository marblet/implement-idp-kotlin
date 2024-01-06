package com.marblet.idp.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    val error: String,
    @JsonProperty("error_description")
    val errorDescription: String?,
    val state: String?,
)
