package com.marblet.idp.domain.model

enum class ResponseType(val value: String) {
    CODE("code"),
    TOKEN("token"),
    ;

    companion object {
        fun find(value: String): ResponseType? {
            return ResponseType.entries.find { it.value == value }
        }
    }

    fun hasToken(): Boolean {
        return when (this) {
            TOKEN -> true
            else -> false
        }
    }
}
