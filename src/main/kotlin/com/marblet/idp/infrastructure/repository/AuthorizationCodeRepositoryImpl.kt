package com.marblet.idp.infrastructure.repository

import com.marblet.idp.domain.model.AuthorizationCode
import com.marblet.idp.domain.repository.AuthorizationCodeRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class AuthorizationCodeRepositoryImpl : AuthorizationCodeRepository {
    override fun insert(authorizationCode: AuthorizationCode) {
        AuthorizationCodes.insert {
            it[code] = authorizationCode.code
            it[userId] = authorizationCode.userId.value
            it[clientId] = authorizationCode.clientId.value
            it[scope] = authorizationCode.scopes.joinToString(separator = " ")
            it[redirectUri] = authorizationCode.redirectUri.value
            it[expiration] = authorizationCode.expiration
        }
    }
}

object AuthorizationCodes : Table("authorization_codes") {
    val code = varchar("code", 32)
    val userId = varchar("user_id", 128) references Users.id
    val clientId = varchar("client_id", 128) references Clients.id
    val scope = text("scope")
    val redirectUri = text("redirect_uri")
    val expiration = datetime("expiration")

    override val primaryKey = PrimaryKey(code)
}
