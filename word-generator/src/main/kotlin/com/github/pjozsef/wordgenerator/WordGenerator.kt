package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.*
import java.util.Random

fun generateWord(
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random,
    rules: List<Rule> = listOf(SubstitutionRule(), InlineSubstitutionRule(), MarkovRule(), ReferenceRule()),
    maxDepth: Int = 100
) = generateWord(expression, mappings, random, rules, maxDepth, 0)

private tailrec fun generateWord(
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random,
    rules: List<Rule> = listOf(SubstitutionRule(), InlineSubstitutionRule(), MarkovRule(), ReferenceRule()),
    maxDepth: Int,
    currentDepth: Int
): String {
    check(currentDepth <= maxDepth) { "Reached maximum depth of recursion: $maxDepth" }

    val (first, second) = doTwoIterations(rules, expression, mappings, random)

    return if (first == second) {
        first
    } else {
        generateWord(first, mappings, random, rules, maxDepth, currentDepth + 1)
    }
}

private fun doTwoIterations(rules: List<Rule>, expression: String, mappings: Map<String, List<String>>, random: Random): Pair<String, String> {
    val first = rules.fold(expression) { acc, rule ->
        applyRule(rule, acc, mappings, random)
    }
    val second = rules.fold(first) { acc, rule ->
        applyRule(rule, acc, mappings, random)
    }
    return Pair(first, second)
}

private fun applyRule(
    rule: Rule,
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random
): String {
    return rule.regex.findAll(expression).map {
        val range = it.range
        val value = rule.evaluate(it.rule(), mappings, random)
        range to value
    }.toList().asReversed().fold(expression) { current, (range, value) ->
        current.replaceRange(range, value)
    }
}

private fun MatchResult.rule() =
    this.groups[1]?.value
        ?: error("Could not find rule key for matched sequence: $value")
