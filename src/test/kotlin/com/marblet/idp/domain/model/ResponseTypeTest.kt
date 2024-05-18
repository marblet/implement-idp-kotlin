package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.ResponseType.CODE
import com.marblet.idp.domain.model.ResponseType.TOKEN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ResponseTypeTest {
    @Test
    fun canFindEnumCorrespondingToValue() {
        val actual = ResponseType.find("code")

        assertThat(actual).isEqualTo(CODE)
    }

    @Test
    fun returnNullIfNoEnumFound() {
        val actual = ResponseType.find("hoge")

        assertThat(actual).isNull()
    }

    @Test
    fun returnTrueIfEnumContainsToken() {
        val actual = TOKEN.hasToken()

        assertThat(actual).isTrue()
    }

    @Test
    fun returnFalseIfEnumNotContainToken() {
        val actual = CODE.hasToken()

        assertThat(actual).isFalse()
    }
}
