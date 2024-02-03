package com.marblet.idp.domain.model

data class TokenScopes(val value: Set<String>) {
    companion object {
        fun fromSpaceSeparatedString(scope: String) = TokenScopes(scope.split(" ").toSet())
    }

    fun toSpaceSeparatedString() = value.joinToString(" ")
}
