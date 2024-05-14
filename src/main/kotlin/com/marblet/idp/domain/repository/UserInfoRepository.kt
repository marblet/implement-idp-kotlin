package com.marblet.idp.domain.repository

import com.marblet.idp.domain.model.UserId
import com.marblet.idp.domain.model.UserInfo

interface UserInfoRepository {
    fun get(userId: UserId): UserInfo?
}
