package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.wordgenerator.util.Scrambler
import java.util.*

class ScrambleRule(
    prefix: String = "!",
    val scrambler: Scrambler = Scrambler()
) : Rule {
    private val ruleRegexString = "((?<rule>[\\w\\s|]+)(,(?<params>[\\s0-9\\-.]+))?)"
    private val _regex: Regex =
        Regex("$prefix\\{$ruleRegexString}")

    override val regex: Regex
        get() = _regex

    private val ruleRegex = Regex(ruleRegexString)
    private val rangeRegex = Regex("(?<range>(?<min>[1-9][0-9]*)?-(?<max>[1-9][0-9]*)?)")

    override fun evaluate(rule: String, mappings: Map<String, List<String>>, random: Random): String {
        scrambler.random = random

        val match = ruleRegex.matchEntire(rule) ?: error("Unparsable rule: $rule")
        val ruleSection = match.groups["rule"]?.value ?: error("Cannot find rule!")
        val (range) = parseParams(match.groups["params"]?.value ?: "")

        return ruleSection.split("|").let {
            val randomValue = it[random.nextInt(it.size)].replace(Regex("\\s"), "")
            mappings[randomValue]?.let { mappingList ->
                mappingList[random.nextInt(mappingList.size)]
            } ?: randomValue
        }.let(scrambler::scramble).let {
            if (range != null) {
                val min = range.min ?: 1
                val max = range.max ?: it.length
                val randomLength = random.nextInt(max-min)+min
                it.take(randomLength)
            } else {
                it
            }
        }
    }

    private data class RangeParam(val min: Int?, val max: Int?)
    private data class ScrambleParams(val range: RangeParam?, val ratio: Double?)

    private fun parseParams(rawParams: String): ScrambleParams {
        val inputParams = rawParams.replace(Regex("\\s"), "")
        val range = rangeRegex.matchEntire(inputParams)?.let {
            RangeParam(
                it.min(),
                it.max()
            )
        }
        return ScrambleParams(range, null)
    }

    private fun MatchResult.min() = groups["min"]?.value?.toInt()
    private fun MatchResult.max() = groups["max"]?.value?.toInt()
}
