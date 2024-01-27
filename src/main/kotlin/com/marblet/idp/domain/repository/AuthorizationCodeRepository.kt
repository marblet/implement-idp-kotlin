package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.AuthorizationCode

interface AuthorizationCodeRepository {
    fun get(code: String): AuthorizationCode?

    fun insert(authorizationCode: AuthorizationCode)

    fun delete(authorizationCode: AuthorizationCode)
}
