package com.marblet.idp.infrastructure

import org.jetbrains.exposed.sql.Table

class ClientRepository {
}

object Clients: Table() {
    val id = varchar("id", 128)
    val secret = varchar("username", 128)
    val redirectUris = text("redirect_uris").nullable()

    override val primaryKey = PrimaryKey(id)
}
