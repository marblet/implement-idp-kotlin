package com.marblet.idp.domain.service

interface HashingService {
    fun hash(rawValue: String): String

    fun matches(
        rawValue: String,
        hashedValue: String,
    ): Boolean
}
