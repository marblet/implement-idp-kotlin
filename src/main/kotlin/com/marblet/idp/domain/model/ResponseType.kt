package com.marblet.idp.domain.model

enum class ResponseType(val value: String) {
    CODE("code"),
    TOKEN("token"),
    IDTOKEN("id_token"),
    IDTOKEN_TOKEN("id_token token"),
    CODE_IDTOKEN("code id_token"),
    CODE_TOKEN("code token"),
    CODE_IDTOKEN_TOKEN("code id_token token"),
    ;

    companion object {
        fun find(value: String): ResponseType? {
            return ResponseType.entries.find { it.value == value }
        }
    }

    fun hasCode(): Boolean {
        return when (this) {
            CODE, CODE_IDTOKEN, CODE_TOKEN, CODE_IDTOKEN_TOKEN -> true
            else -> false
        }
    }

    fun hasToken(): Boolean {
        return when (this) {
            TOKEN, IDTOKEN_TOKEN, CODE_TOKEN, CODE_IDTOKEN_TOKEN -> true
            else -> false
        }
    }

    fun hasIdToken(): Boolean {
        return when (this) {
            IDTOKEN, IDTOKEN_TOKEN, CODE_IDTOKEN, CODE_IDTOKEN_TOKEN -> true
            else -> false
        }
    }

    fun requiresOpenidScope(): Boolean {
        return when (this) {
            IDTOKEN, IDTOKEN_TOKEN, CODE_TOKEN, CODE_IDTOKEN, CODE_IDTOKEN_TOKEN -> true
            else -> false
        }
    }
}
