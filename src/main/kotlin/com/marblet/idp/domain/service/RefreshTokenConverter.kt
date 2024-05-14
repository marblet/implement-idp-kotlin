package com.marblet.idp.domain.service

import com.marblet.idp.domain.model.RefreshTokenPayload

interface RefreshTokenConverter {
    fun encode(payload: RefreshTokenPayload): String

    fun decode(refreshToken: String): RefreshTokenPayload?
}
