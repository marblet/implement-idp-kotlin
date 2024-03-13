package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.Consent
import com.marblet.idp.domain.model.UserId

interface ConsentRepository {
    fun get(
        userId: UserId,
        clientId: ClientId,
    ): Consent?
}
