package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.AuthorizationCode

interface AuthorizationCodeRepository {
    fun insert(authorizationCode: AuthorizationCode)
}
