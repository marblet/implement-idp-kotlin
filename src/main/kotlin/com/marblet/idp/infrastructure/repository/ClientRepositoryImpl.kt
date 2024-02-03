package com.marblet.idp.infrastructure.repository

import com.marblet.idp.domain.model.Client
import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.ClientScopes
import com.marblet.idp.domain.repository.ClientRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ClientRepositoryImpl : ClientRepository {
    override fun get(clientId: ClientId): Client? {
        return Clients.select {
            Clients.id eq clientId.value
        }.firstOrNull()?.let {
            Client(
                clientId = ClientId(it[Clients.id]),
                secret = it[Clients.secret],
                redirectUris = it[Clients.redirectUris].split(" ").toSet(),
                name = it[Clients.name],
                scopes = ClientScopes.fromSpaceSeparatedString(it[Clients.scopes]),
            )
        }
    }
}

object Clients : Table("clients") {
    val id = varchar("id", 128)
    val secret = varchar("secret", 128).nullable()
    val redirectUris = text("redirect_uris")
    val name = varchar("name", 128)
    val scopes = text("scopes")

    override val primaryKey = PrimaryKey(id)
}
