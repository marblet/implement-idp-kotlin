package com.marblet.idp.domain.model

import com.marblet.idp.domain.service.HashingService

data class User(val id: UserId, val username: String, val password: HashedPassword) {
    fun validate(
        unauthenticatedUser: UnauthenticatedUser,
        hashingService: HashingService,
    ): Boolean {
        return username == unauthenticatedUser.username &&
            password.matches(unauthenticatedUser.password, hashingService)
    }
}

data class UserId(val value: String)
