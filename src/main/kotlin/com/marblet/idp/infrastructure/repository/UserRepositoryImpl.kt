package com.marblet.idp.infrastructure.repository

import com.marblet.idp.domain.model.HashedPassword
import com.marblet.idp.domain.model.User
import com.marblet.idp.domain.model.UserId
import com.marblet.idp.domain.repository.UserRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserRepositoryImpl : UserRepository {
    override fun get(id: String): User? {
        return Users.select {
            Users.id eq id
        }.firstOrNull()?.let {
            User(
                id = UserId(it[Users.id]),
                username = it[Users.username],
                password = HashedPassword(it[Users.password]),
            )
        }
    }

    override fun findByUsername(username: String): User? {
        return Users.select {
            Users.username eq username
        }.firstOrNull()?.let {
            User(
                id = UserId(it[Users.id]),
                username = it[Users.username],
                password = HashedPassword(it[Users.password]),
            )
        }
    }
}

object Users : Table("users") {
    val id = varchar("id", 128)
    val username = varchar("username", 128)
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}
