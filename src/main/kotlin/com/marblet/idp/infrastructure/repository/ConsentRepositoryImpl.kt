package com.marblet.idp.infrastructure.repository

import com.marblet.idp.domain.model.ClientId
import com.marblet.idp.domain.model.Consent
import com.marblet.idp.domain.model.ConsentedScopes
import com.marblet.idp.domain.model.UserId
import com.marblet.idp.domain.repository.ConsentRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ConsentRepositoryImpl : ConsentRepository {
    override fun get(
        userId: UserId,
        clientId: ClientId,
    ): Consent? {
        return Consents.select {
            (Consents.userId eq userId.value) and (Consents.clientId eq clientId.value)
        }.firstOrNull()?.let {
            Consent(
                userId = UserId(it[Consents.userId]),
                clientId = ClientId(it[Consents.clientId]),
                scopes = ConsentedScopes.fromSpaceSeparatedString(it[Consents.scopes]),
            )
        }
    }
}

object Consents : Table("consents") {
    val userId = varchar("user_id", 128)
    val clientId = varchar("client_id", 128)
    val scopes = text("scopes")

    override val primaryKey = PrimaryKey(userId, clientId)
}
