package com.marblet.idp.infrastructure

import org.jetbrains.exposed.sql.Table

class UserRepository {
}

object Users : Table() {
    val id = varchar("id", 128)
    val username = varchar("username", 128)
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}
