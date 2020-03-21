package com.github.pjozsef.wordgenerator.rule

import java.util.Random

class InlineSubstitutionRule(
    prefix: String = "#"
) : Rule {
    private val _regex: Regex = Regex("$prefix\\{([\\w|\\s]+)}")

    override val regex = _regex

    override fun evaluate(
        rule: String,
        mappings: Map<String, List<String>>,
        random: Random
    ): String {
        return rule.split("|").map {
            it.replace(Regex("\\s+"), "")
        }.filter {
            it.isNotBlank()
        }.let {
            it[random.nextInt(it.size)]
        }
    }
}
