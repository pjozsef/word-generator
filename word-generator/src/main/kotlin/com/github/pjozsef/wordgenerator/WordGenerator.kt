package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.InlineSubstitutionRule
import com.github.pjozsef.wordgenerator.rule.MarkovRule
import com.github.pjozsef.wordgenerator.rule.Rule
import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import java.util.Random

fun generateWord(
    expression: String,
    mappings: Map<String, List<String>>,
    random: Random,
    rules: List<Rule> = listOf(SubstitutionRule(), InlineSubstitutionRule(), MarkovRule())
): String {
    return rules.flatMap { rule ->
            val occurences = rule.regex.findAll(expression).toList()
            val ruleRepetition = List(occurences.size) { rule }
            ruleRepetition.zip(occurences)
        }.map { (rule, match) ->
            val range = match.range
            val value = rule.evaluate(match.rule(), mappings, random)
            range to value
        }.toList().asReversed().fold(expression) { current, (range, value) ->
            current.replaceRange(range, value)
        }
}

private fun MatchResult.rule() =
    this.groups[1]?.value
        ?: error("Could not find rule key for matched sequence: $value")
