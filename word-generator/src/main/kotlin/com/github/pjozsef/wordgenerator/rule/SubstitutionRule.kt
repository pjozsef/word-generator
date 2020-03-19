package com.github.pjozsef.wordgenerator.rule

import java.util.*

class SubstitutionRule : Rule {
    override fun evaluate(
        rule: String,
        mappings: Map<String, List<String>>,
        random: Random
    ): String {
        return rule.split("+").flatMap {
            mappings.getValue(it)
        }.let {
            it[random.nextInt(it.size)]
        }
    }
}
