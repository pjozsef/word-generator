package com.github.pjozsef.wordgenerator

import java.util.Random

fun generateWord(
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random,
    rulePrefix: String = "#"
): String {

    val regex = Regex("$rulePrefix\\{([\\w+]+)}")

    return regex.findAll(expression).map {
        val range = it.range
        val value = getSubstitution(it, mappings, random)
        range to value
    }.toList().asReversed().fold(expression) { current, (range, value) ->
        current.replaceRange(range, value)
    }
}

private fun getSubstitution(
    matchResult: MatchResult,
    mapping: Map<String, List<String>>,
    random: Random
): String {
    return matchResult.extractRule().split("+").flatMap {
        mapping.getValue(it)
    }.let {
        it[random.nextInt(it.size)]
    }
}

private fun MatchResult.extractRule() =
    this.groups[1]?.value
        ?: error("Could not find rule key for mathced sequence: $value")
