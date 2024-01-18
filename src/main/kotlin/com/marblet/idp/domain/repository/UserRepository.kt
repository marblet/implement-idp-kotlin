package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.User

interface UserRepository {
    fun get(id: String): User?

    fun findByUsername(username: String): User?
}
