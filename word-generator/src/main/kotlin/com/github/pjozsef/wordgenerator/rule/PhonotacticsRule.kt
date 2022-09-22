package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.randomtree.LeafNode
import com.github.pjozsef.randomtree.RandomTree
import com.github.pjozsef.randomtree.io.RandomTreeReader
import com.github.pjozsef.randomtree.io.readTreeFromString
import com.github.pjozsef.wordgenerator.rule.Phase.GENERATE
import com.github.pjozsef.wordgenerator.rule.Phase.SUBSTITUTE
import java.util.Random

class PhonotacticsRule(
    prefix: String = "@",
    val terminalOperator: String = "#"
) : Rule {
    private val ruleRegexString = "((?<rule>[\\w\\s]+))"
    private val _regex: Regex =
        Regex("$prefix\\{$ruleRegexString}")

    override val regex: Regex
        get() = _regex

    override fun evaluate(
        rule: String,
        mappings: Map<String, List<String>>,
        random: Random
    ): String {
        TODO("Not yet implemented")
    }

    fun eval(rule: String, trees: Map<String, RandomTree<String>>): String {

        return ""
    }
}

private data class PhonotacticsResult(
    val result: String,
    val rawResult: String,
    val phonotactics: String
)

fun main() {
    val rules = """
        
    """.trimIndent()
    val phonetics = """
        
    """.trimIndent()
    val phoneticGroupReplacements = emptyList<Pair<Regex, String>>()
    val phoneticReplacements = emptyList<Pair<Regex, String>>()



    val filters = emptyList<(String) -> Boolean>()


    //Falloff: 0.3, avg len: 4.496, diff: 13
    //Falloff: 0.35, avg len: 4.704, diff: 12
    //Falloff: 0.4, avg len: 5.202, diff: 30
    //Falloff: 0.45, avg len: 5.6, diff: 21
    //Falloff: 0.5, avg len: 5.728, diff: 22
    //Falloff: 0.55, avg len: 6.564, diff: 26
    //Falloff: 0.6, avg len: 7.008, diff: 24
    //Falloff: 0.65, avg len: 7.892, diff: 28
    //Falloff: 0.7, avg len: 9.256, diff: 46
    //Falloff: 0.75, avg len: 10.912, diff: 52
    //Falloff: 0.8, avg len: 14.982, diff: 103
    //Falloff: 0.85, avg len: 17.5, diff: 129
    //Falloff: 0.9, avg len: 26.392, diff: 167
    val random = Random()
    val fallOff = 0.4
    val minLength = 1
    val maxLength = 5
    val showSyllables = true
    val amount = 2000
    val ruleTrees = readTreeFromString(
        rules,
        RandomTreeReader.IDENTITY_MAPPER,
        RandomTreeReader.CONCAT_COMBINER(""),
        random,
        adjustRelativeWeight = true
    )
    val phoneticsTrees = readTreeFromString(
        phonetics,
        RandomTreeReader.IDENTITY_MAPPER,
        RandomTreeReader.CONCAT_COMBINER(""),
        random,
        adjustRelativeWeight = true
    ).let {
        if (showSyllables) {
            mapOf("." to LeafNode(".")) + it
        } else {
            it
        }
    }


    generateSequence(Unit) { }.map {
        val phonotactics = generate(ruleTrees, fallOff, minLength, maxLength, random, showSyllables).let {
            handleReplacements(it, phoneticGroupReplacements)
        }
        val rawResult = substitute(phonotactics, phoneticsTrees)
        phonotactics to rawResult
    }.filter { (_, rawResult) ->
        filters.all { it(rawResult) }
    }.distinct().take(amount).map { (phonotactics, rawResult) ->
        PhonotacticsResult(
            result = handleReplacements(rawResult, phoneticReplacements),
            rawResult = rawResult,
            phonotactics = phonotactics
        )
    }.chunked(10).forEach { chunk ->
        chunk.joinToString("\t") { it.result }.let(::println)
    }
}

fun generate(
    trees: Map<String, RandomTree<String>>,
    fallOff: Double,
    minLength: Int,
    maxLength: Int,
    random: Random,
    showSyllables: Boolean,
) = generate(
    trees.getValue("START").value,
    trees,
    fallOff,
    minLength,
    maxLength,
    random,
    showSyllables,
)

tailrec fun generate(
    current: String,
    trees: Map<String, RandomTree<String>>,
    fallOff: Double,
    minLength: Int,
    maxLength: Int,
    random: Random,
    showSyllables: Boolean,
    endChar: String = "#",
    iter: Int = 1
): String {
    val roll = random.nextDouble()
    val withinBounds = iter < minLength || iter < maxLength && roll <= fallOff
    val notEnd = !current.endsWith(endChar)
    return if (withinBounds && notEnd) {
        val nextTree = trees.getValue(findNextKey(current, trees, GENERATE) { drop(1) })
        val syllableBorder = if (showSyllables) "." else ""
        generate(
            current + syllableBorder + nextTree.value,
            trees,
            fallOff,
            minLength,
            maxLength,
            random,
            showSyllables,
            endChar,
            iter + 1
        )
    } else {
        current.replace(endChar, "")
    }
}

fun substitute(rules: String, trees: Map<String, RandomTree<String>>): String {
    tailrec fun substitute(
        result: String,
        rules: String,
        trees: Map<String, RandomTree<String>>
    ): String {
        return when {
            rules.isEmpty() -> result
            else -> {
                val key = findNextKey(rules, trees, SUBSTITUTE) { dropLast(1) }
                val newResult = trees.getValue(key).value

                substitute("$result$newResult", rules.drop(key.length), trees)
            }
        }
    }

    return substitute("", rules, trees)
}

tailrec fun handleReplacements(
    word: String,
    replacements: List<Pair<Regex, String>>,
    iter: Int = 0
): String {
    val replaced = replacements.fold(word) { acc, (regex, repl) ->
        acc.replace(regex, repl)
    }
    return when {
        iter > 100 || replaced == word -> replaced
        else -> handleReplacements(replaced, replacements, iter + 1)
    }
}

enum class Phase { GENERATE, SUBSTITUTE }

fun findNextKey(
    current: String,
    trees: Map<String, RandomTree<String>>,
    phase: Phase,
    defaultKey: String = "_",
    dropCharacter: String.() -> String,
): String {
    tailrec fun findNextKey(
        original: String,
        current: String,
        trees: Map<String, RandomTree<String>>
    ): String {
        return when {
            current.isEmpty() && trees.containsKey(defaultKey) -> defaultKey
            current.isEmpty() -> error("[$phase] Could not find match for $original among tree keys and no default fallback rule exists!")
            trees.containsKey(current) -> current
            else -> findNextKey(original, current.dropCharacter(), trees)
        }
    }
    return findNextKey(current, current, trees)
}
