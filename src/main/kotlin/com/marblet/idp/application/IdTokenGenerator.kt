package com.marblet.idp.application

import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.model.IdTokenPayload
import com.marblet.idp.domain.service.IdTokenConverter
import org.springframework.stereotype.Service

@Service
class IdTokenGenerator(private val idTokenConverter: IdTokenConverter) {
    fun generate(authorizationCode: AuthorizationCode): String? {
        if (!authorizationCode.scopes.hasOpenidScope()) {
            return null
        }
        return IdTokenPayload.generate(authorizationCode)?.let { idTokenConverter.encode(it) }
    }
}
