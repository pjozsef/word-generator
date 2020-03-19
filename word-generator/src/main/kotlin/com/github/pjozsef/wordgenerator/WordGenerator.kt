package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import java.util.Random

fun generateWord(
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random,
    substitutionRule: SubstitutionRule = SubstitutionRule(),
    substitutionRulePrefix: String = "#"
): String {

    val regex = Regex("$substitutionRulePrefix\\{([\\w+]+)}")

    return regex.findAll(expression).map {
        val range = it.range
        val value = substitutionRule.evaluate(it.rule(), mappings, random)
        range to value
    }.toList().asReversed().fold(expression) { current, (range, value) ->
        current.replaceRange(range, value)
    }
}

private fun MatchResult.rule() =
    this.groups[1]?.value
        ?: error("Could not find rule key for mathced sequence: $value")
