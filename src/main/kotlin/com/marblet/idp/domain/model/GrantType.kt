package com.marblet.idp.domain.model

enum class GrantType(val value: String) {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
}
