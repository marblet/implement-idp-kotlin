package com.marblet.idp.application

import com.marblet.idp.domain.model.Client
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.ClientScopes
import com.marblet.idp.domain.repository.ClientRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ClientBasicAuthenticationTest {
    private val clientId = ClientId("id")
    private val client =
        Client(
            clientId = clientId,
            secret = "secret",
            redirectUris = setOf("url1", "url2"),
            name = "client name",
            scopes = ClientScopes(setOf("scope1", "scope2")),
        )
    private val clientRepository =
        mock<ClientRepository> {
            on { get(clientId) } doReturn client
        }
    private val target = ClientBasicAuthentication(clientRepository)

    @Test
    fun returnClientWhenAuthenticated() {
        val actual = target.authenticate("Basic aWQ6c2VjcmV0") // id:secret

        assertThat(actual).isEqualTo(client)
    }

    @Test
    fun returnNullWhenClientNotExist() {
        val actual = target.authenticate("Basic aW52YWxpZC1pZDpzZWNyZXQ=") // invalid-id:secret

        assertThat(actual).isNull()
    }

    @Test
    fun returnNullWhenSecretDiffers() {
        val target = ClientBasicAuthentication(clientRepository)

        val actual = target.authenticate("Basic aWQ6cGFzc3dvcmQ=") // id:password

        assertThat(actual).isNull()
    }

    @Test
    fun ifNotBasicAuthThenReturnNull() {
        val actual = target.authenticate("Invalid header")

        assertThat(actual).isNull()
    }
}
