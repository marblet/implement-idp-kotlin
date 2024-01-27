package com.marblet.idp.application

import com.marblet.idp.domain.model.Client
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.repository.ClientRepository
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class ClientBasicAuthentication(private val clientRepository: ClientRepository) {
    fun authenticate(authorizationHeader: String): Client? {
        if (!authorizationHeader.startsWith("Basic ")) {
            return null
        }
        val decodedHeader =
            Base64.getDecoder().decode(authorizationHeader.substring(6).toByteArray())
                .toString(Charsets.UTF_8)
                .split(":")
        if (decodedHeader.size != 2) {
            return null
        }
        val requestClientId = decodedHeader[0]
        val secret = decodedHeader[1]
        val client = clientRepository.get(ClientId(requestClientId)) ?: return null
        if (secret != client.secret) {
            return null
        }
        return client
    }
}
