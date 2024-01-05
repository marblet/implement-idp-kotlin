package com.marblet.idp.infrastructure

import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.springframework.stereotype.Repository

@Repository
class AuthorizationCodeRepositoryImpl : AuthorizationCodeRepository

object AuthorizationCodes : Table("authorization_codes") {
    val code = varchar("code", 32)
    val userId = varchar("user_id", 128) references Users.id
    val clientId = varchar("client_id", 128) references Clients.id
    val scope = text("redirect_uris").nullable()
    val expiration = datetime("expiration")

    override val primaryKey = PrimaryKey(code)
}
