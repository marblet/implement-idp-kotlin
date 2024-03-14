package com.marblet.idp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PromptSetTest {
    @Test
    fun fromPromptNone() {
        val actual = PromptSet.from("none")

        assertThat(actual).isEqualTo(PromptSet(setOf(Prompt.NONE)))
    }

    @Test
    fun fromPromptLoginAndConsent() {
        val actual = PromptSet.from("login consent")

        assertThat(actual).isEqualTo(PromptSet(setOf(Prompt.LOGIN, Prompt.CONSENT)))
    }

    @Test
    fun fromPromptSelectAccount() {
        val actual = PromptSet.from("select_account")

        assertThat(actual).isEqualTo(PromptSet(setOf(Prompt.SELECT_ACCOUNT)))
    }

    @Test
    fun fromNull() {
        val actual = PromptSet.from(null)

        assertThat(actual).isEqualTo(PromptSet(setOf()))
    }
}
