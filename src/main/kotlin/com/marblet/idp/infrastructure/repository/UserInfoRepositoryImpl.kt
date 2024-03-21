package com.marblet.idp.infrastructure.repository

import com.marblet.idp.domain.model.UserId
import com.marblet.idp.domain.model.UserInfo
import com.marblet.idp.domain.repository.UserInfoRepository
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class UserInfoRepositoryImpl : UserInfoRepository {
    override fun get(userId: UserId): UserInfo? {
        return Userinfos.select {
            Userinfos.userId eq userId.value
        }.firstOrNull()?.let {
            UserInfo(
                userId = userId,
                name = it[Userinfos.name],
                email = it[Userinfos.email],
                phoneNumber = it[Userinfos.phoneNumber],
                address = it[Userinfos.address],
            )
        }
    }
}

object Userinfos : Table("userinfos") {
    val userId = varchar("user_id", 128) references Users.id
    val name = varchar("name", 256).nullable()
    val email = varchar("email", 256).nullable()
    val phoneNumber = varchar("phone_number", 20).nullable()
    val address = varchar("address", 256).nullable()

    override val primaryKey = PrimaryKey(userId)
}
