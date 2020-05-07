package com.github.pjozsef.wordgenerator.rule

import java.util.*

class SubstitutionRule(
    prefix: String = "#"
) : Rule {

    private val _regex: Regex = Regex("$prefix\\{([\\w+\\s]+)}")

    override val regex: Regex
        get() = _regex

    override fun evaluate(
        rule: String,
        mappings: Map<String, List<String>>,
        random: Random
    ): String {
        return rule.split("+").map {
            it.trim()
        }.filter {
            it.isNotBlank()
        }.flatMap {
            mappings.getValue(it)
        }.let {
            it[random.nextInt(it.size)]
        }
    }
}
