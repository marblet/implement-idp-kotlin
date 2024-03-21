package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.UserInfoScope.ADDRESS
import com.marblet.idp.domain.model.UserInfoScope.EMAIL
import com.marblet.idp.domain.model.UserInfoScope.PHONE
import com.marblet.idp.domain.model.UserInfoScope.PROFILE

data class UserInfo(
    val userId: UserId,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val address: String?,
) {
    fun toUserInfoResponse(userInfoRequestScopes: UserInfoRequestScopes): UserInfoResponse {
        return UserInfoResponse(
            sub = userId.value,
            name = if (userInfoRequestScopes.contains(PROFILE)) name else null,
            email = if (userInfoRequestScopes.contains(EMAIL)) email else null,
            phoneNumber = if (userInfoRequestScopes.contains(PHONE)) phoneNumber else null,
            address = if (userInfoRequestScopes.contains(ADDRESS)) address else null,
        )
    }
}

data class UserInfoResponse(
    val sub: String,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val address: String?,
)
