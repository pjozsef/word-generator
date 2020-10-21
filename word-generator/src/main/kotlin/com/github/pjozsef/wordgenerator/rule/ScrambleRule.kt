package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.wordgenerator.util.Scrambler
import java.util.Random

class ScrambleRule(
    prefix: String = "!",
    val scrambler: Scrambler = Scrambler()
) : Rule {
    private val vowels = listOf('a', 'i', 'e', 'o', 'u', 'y')
    private val ruleRegexString = "((?<rule>[\\w\\s|]+)(,(?<params>[\\s0-9\\-.,]+))?)"
    private val _regex: Regex =
        Regex("$prefix\\{$ruleRegexString}")

    override val regex: Regex
        get() = _regex

    private val ruleRegex = Regex(ruleRegexString)
    private val rangeRegex = Regex("(?<range>(?<min>[1-9][0-9]*)?-(?<max>[1-9][0-9]*)?)")
    private val ratioRegex = Regex("[0-9]+\\.[0-9]+")

    override fun evaluate(rule: String, mappings: Map<String, List<String>>, random: Random): String {
        scrambler.random = random

        val match = ruleRegex.matchEntire(rule) ?: error("Unparsable rule: $rule")
        val ruleSection = match.groups["rule"]?.value ?: error("Cannot find rule!")
        val (range, ratio) = parseParams(match.groups["params"]?.value ?: "")

        return ruleSection.split("|").let {
            val randomValue = it[random.nextInt(it.size)].replace(Regex("\\s"), "")
            mappings[randomValue]?.let { mappingList ->
                mappingList[random.nextInt(mappingList.size)]
            } ?: randomValue
        }.let {
            if (ratio != null) {
                val (vowels, consonants) = it.partition {
                    vowels.contains(it)
                }.let {
                    scrambler.scramble(it.first).toList() to scrambler.scramble(it.second).toList()
                }
                combineWithRatio(vowels, consonants, ratio, random)
            } else {
                scrambler.scramble(it)
            }
        }.let {
            if (range != null) {
                val min = range.min ?: 1
                val max = range.max ?: it.length
                val randomLength = random.nextInt(max - min) + min
                it.take(randomLength)
            } else {
                it
            }
        }
    }

    private fun combineWithRatio(vowels: List<Char>, consonants: List<Char>, ratio: Double, random: Random): String {
        tailrec fun combine(acc: String, vowels: List<Char>, consonants: List<Char>): String = when {
            vowels.isEmpty() -> acc + consonants.joinToString("")
            consonants.isEmpty() -> acc + vowels.joinToString("")
            else -> {
                if (random.nextDouble() < ratio) {
                    combine(acc + vowels.first(), vowels.drop(1), consonants)
                } else {
                    combine(acc + consonants.first(), vowels, consonants.drop(1))
                }
            }
        }
        return combine("", vowels, consonants)
    }

    private data class RangeParam(val min: Int?, val max: Int?)
    private data class ScrambleParams(val range: RangeParam?, val ratio: Double?)

    private fun parseParams(rawParams: String) =
        rawParams.split(",")
            .map {
                it.replace(Regex("\\s"), "")
            }.filter {
                it.isNotBlank()
            }.fold(ScrambleParams(null, null)) { acc, param ->
                when {
                    rangeRegex.matches(param) -> {
                        rangeRegex.matchEntire(param)?.let {
                            acc.copy(range = RangeParam(it.min(), it.max()))
                        } ?: error("Unparsable param: $param")
                    }
                    ratioRegex.matches(param) -> {
                        ratioRegex.matchEntire(param)?.let {
                            acc.copy(ratio = it.value.toDouble())
                        } ?: error("Unparsable param: $param")
                    }
                    else -> error("Unparsable param: $param")
                }
            }

    private fun MatchResult.min() = groups["min"]?.value?.toInt()
    private fun MatchResult.max() = groups["max"]?.value?.toInt()
}
