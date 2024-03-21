package com.marblet.idp.domain.model

enum class UserInfoError(val error: String) {
    INVALID_REQUEST("invalid_request"),
    INVALID_TOKEN("invalid_token"),
    INSUFFICIENT_SCOPE("insufficient_scope"),
}
