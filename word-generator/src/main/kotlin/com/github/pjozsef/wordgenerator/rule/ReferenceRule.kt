package com.github.pjozsef.wordgenerator.rule

import java.util.*

class ReferenceRule(
    prefix: String = ":"
) : Rule {
    private val refsKey = "refs"

    private val _regex = Regex("$prefix\\{\\w+}")

    override val regex: Regex
        get() = _regex

    override fun evaluate(rule: String, mappings: Map<String, List<String>>, random: Random): String {
        val prefix = "$rule="
        return mappings.getValue(refsKey)
            .find {
                it.startsWith(prefix)
            }?.drop(prefix.length)
            ?: error("Could not find reference: $rule")
    }
}
