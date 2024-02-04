package com.marblet.idp.domain.model

enum class OpenidScope(val value: String) {
    OPENID("openid"),
}

enum class UserInfoScope(val value: String) {
    PROFILE("profile"),
    EMAIL("email"),
    ADDRESS("address"),
    PHONE("phone"),
}
