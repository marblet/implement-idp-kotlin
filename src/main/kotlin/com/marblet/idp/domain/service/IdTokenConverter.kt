package com.marblet.idp.domain.service

import com.marblet.idp.domain.model.IdTokenPayload

interface IdTokenConverter {
    fun encode(payload: IdTokenPayload): String
}
