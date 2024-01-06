package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.Client
import com.marblet.idp.domain.model.ClientId

interface ClientRepository {
    fun get(clientId: ClientId): Client?
}
