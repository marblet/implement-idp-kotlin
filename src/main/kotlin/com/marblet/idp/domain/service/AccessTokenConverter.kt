package com.marblet.idp.domain.service

import com.marblet.idp.domain.model.AccessTokenPayload

interface AccessTokenConverter {
    fun encode(payload: AccessTokenPayload): String

    fun decode(accessToken: String): AccessTokenPayload?
}
