package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.markovchain.MarkovChain
import com.github.pjozsef.markovchain.Transition
import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.constraint.parseConstraint
import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.asDice
import com.github.pjozsef.wordgenerator.cache.Cache
import java.util.Random

typealias OrderAndWords = Pair<Int, List<String>>

class MarkovRule(
    prefix: String = "\\*",
    val markovChainFactory: (Transition, String, Int) -> MarkovChain = ::defaultFactory,
    private val cache: Cache<OrderAndWords, Transition>? = null
) : Rule {
    private val _regex = Regex("$prefix\\{([^{}]+)}")

    override val regex: Regex
        get() = _regex

    private val ruleRegex = Regex("(?<rule>[\\w+\\s]+)(#(?<order>\\d+))?(,(?<constraints>.+))?")

    override fun evaluate(rule: String, mappings: Map<String, List<String>>, random: Random): String {
        return ruleRegex.matchEntire(rule)?.let {
            val order = it.groups["order"]?.value?.toInt() ?: 1
            val markovChain = getMarkovChain(it, order, mappings, random)
            val constraints = it.groups["constraints"]?.value?.let(::parseConstraint)
                ?: Constraints()
            markovChain.generate(order, 1, constraints).chooseRandom(random) ?: "_NO_RESULT_"
        } ?: error("Unparsable rule: $rule")
    }

    private fun getMarkovChain(
        matchResult: MatchResult,
        order: Int,
        mappings: Map<String, List<String>>,
        random: Random
    ): MarkovChain {
        val key = matchResult.groups["rule"]?.value ?: error("Rule key not found")
        val words = key.split("+")
            .map(String::trim).filter(String::isNotBlank)
            .flatMap { mappings.getValue(it) }

        val transition = cache?.get(order to words) ?: TransitionRule
            .fromWords(words, order, "#")
            .asDice(random)
            .also {
                cache?.set(order to words, it)
            }

        return markovChainFactory(transition, "#", 1_000_000)
    }

    private fun <T> Collection<T>.chooseRandom(random: Random) = this.toList().getOrNull(random.nextInt(size))

}

private fun defaultFactory(transition: Transition, end: String = "#", maxRetries: Int = 1_000_000) =
    MarkovChain(transition, end, maxRetries)
