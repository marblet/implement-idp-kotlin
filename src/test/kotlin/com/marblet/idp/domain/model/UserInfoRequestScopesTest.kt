package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.UserInfoScope.ADDRESS
import com.marblet.idp.domain.model.UserInfoScope.EMAIL
import com.marblet.idp.domain.model.UserInfoScope.PHONE
import com.marblet.idp.domain.model.UserInfoScope.PROFILE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserInfoRequestScopesTest {
    @Test
    fun generateInstanceExtractingUserInfoScopeFromTokenScopes() {
        val tokenScopes = TokenScopes(setOf("profile", "email", "address", "phone", "a", "b"))

        val actual = UserInfoRequestScopes.generate(tokenScopes)

        val expect = UserInfoRequestScopes(setOf(PROFILE, EMAIL, ADDRESS, PHONE))
        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun returnNullIfNoUserInfoScopeContained() {
        val tokenScopes = TokenScopes(setOf("a", "b"))

        val actual = UserInfoRequestScopes.generate(tokenScopes)

        assertThat(actual).isNull()
    }

    @Test
    fun returnTrueIfInstanceContainsUserInfoScope() {
        val target = UserInfoRequestScopes.generate(TokenScopes(setOf("profile", "email")))

        val actual = target?.contains(PROFILE)

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfInstanceNotContainUserInfoScope() {
        val target = UserInfoRequestScopes.generate(TokenScopes(setOf("profile", "email")))

        val actual = target?.contains(ADDRESS)

        assertThat(actual).isFalse()
    }
}
