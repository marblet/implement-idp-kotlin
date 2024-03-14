package com.marblet.idp.domain.model

data class PromptSet(val prompts: Set<Prompt>) {
    companion object {
        fun from(prompt: String?): PromptSet {
            if (prompt == null) {
                return PromptSet(setOf())
            }
            return PromptSet(prompt.split(" ").mapNotNull { Prompt.find(it) }.toSet())
        }
    }

    fun contains(prompt: Prompt): Boolean {
        return prompts.contains(prompt)
    }
}

enum class Prompt(val value: String) {
    NONE("none"),
    LOGIN("login"),
    CONSENT("consent"),
    SELECT_ACCOUNT("select_account"),
    ;

    companion object {
        fun find(value: String): Prompt? {
            return entries.find { it.value == value }
        }
    }
}
