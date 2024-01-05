package com.marblet.idp.infrastructure

import com.marblet.idp.domain.repository.UserRepository
import org.jetbrains.exposed.sql.Table
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl : UserRepository

object Users : Table("users") {
    val id = varchar("id", 128)
    val username = varchar("username", 128)
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}
