package com.marblet.idp.domain.model

import com.marblet.idp.domain.model.ResponseType.CODE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

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

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["CODE", "CODE_IDTOKEN", "CODE_TOKEN", "CODE_IDTOKEN_TOKEN"])
    fun hasCodeReturnTrueIfEnumContainsCode(responseType: ResponseType) {
        val actual = responseType.hasCode()

        assertThat(actual).isTrue()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["TOKEN", "IDTOKEN", "IDTOKEN_TOKEN"])
    fun hasCodeReturnFalseIfEnumNotContainCode(responseType: ResponseType) {
        val actual = responseType.hasCode()

        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["TOKEN", "IDTOKEN_TOKEN", "CODE_TOKEN", "CODE_IDTOKEN_TOKEN"])
    fun hasTokenReturnTrueIfEnumContainsToken(responseType: ResponseType) {
        val actual = responseType.hasToken()

        assertThat(actual).isTrue()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["CODE", "IDTOKEN", "CODE_IDTOKEN"])
    fun hasTokenReturnFalseIfEnumNotContainToken(responseType: ResponseType) {
        val actual = responseType.hasToken()

        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["IDTOKEN", "IDTOKEN_TOKEN", "CODE_IDTOKEN", "CODE_IDTOKEN_TOKEN"])
    fun hasIdTokenReturnTrueIfEnumContainsIdToken(responseType: ResponseType) {
        val actual = responseType.hasIdToken()

        assertThat(actual).isTrue()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["CODE", "TOKEN", "CODE_TOKEN"])
    fun hasIdTokenReturnFalseIfEnumNotContainIdToken(responseType: ResponseType) {
        val actual = responseType.hasIdToken()

        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["IDTOKEN", "IDTOKEN_TOKEN", "CODE_TOKEN", "CODE_IDTOKEN", "CODE_IDTOKEN_TOKEN"])
    fun requiresOpenidScopeReturnTrueIfTriggersHybridFlow(responseType: ResponseType) {
        val actual = responseType.requiresOpenidScope()

        assertThat(actual).isTrue()
    }

    @ParameterizedTest
    @EnumSource(ResponseType::class, names = ["CODE", "TOKEN"])
    fun requiresOpenidScopeReturnFalseIfNotTriggerHybridFlow(responseType: ResponseType) {
        val actual = responseType.requiresOpenidScope()

        assertThat(actual).isFalse()
    }
}
