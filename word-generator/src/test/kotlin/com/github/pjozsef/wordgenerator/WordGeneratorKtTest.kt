package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.Random

class WordGeneratorKtTest : FreeSpec({
    "generateWord" - {
        val random = mock<Random>()
        val mappings = mapOf(
            "a" to listOf("x"),
            "b" to listOf("y"),
            "multipleOptions" to listOf("0", "1", "2", "3")
        )

        "returns expression as is if it does not contain any rules" {
            generateWord(
                "string constant",
                mappings,
                random,
                SubstitutionRule()
            ) shouldBe "string constant"
        }

        "Substitution" - {

            forall(
                row(
                    "Substitution:substitutes rule to corresponding value",
                    "#{a}",
                    "x",
                    "#",
                    listOf(0)
                ),
                row(
                    "multiple substitutions",
                    "#{a}#{b}",
                    "xy",
                    "#",
                    listOf(0)
                ),
                row(
                    "multiple substitutions with constant values in between",
                    "#{a} - #{b}",
                    "x - y",
                    "#",
                    listOf(0)
                ),
                row(
                    "rule can be anywhere in the expression",
                    "prefix#{a}",
                    "prefixx",
                    "#",
                    listOf(0)
                ),
                row(
                    "rule prefix can be changed",
                    "_!{a}#{notRule}",
                    "_x#{notRule}",
                    "!",
                    listOf(0)
                ),
                row(
                    "rule prefix can be longer than 1 character",
                    "longprefix{a}",
                    "x",
                    "longprefix",
                    listOf(0)
                ),
                row(
                    "substitution chosen from a composite rule",
                    "#{a+b}#{a+b}",
                    "xy",
                    "#",
                    listOf(0, 1)
                )
            ) { test, input, expected, rulePrefix, randomIndices ->
                test {
                    whenever(random.nextInt(any())).doReturnConsecutively(randomIndices)
                    generateWord(
                        input,
                        mappings,
                        random,
                        SubstitutionRule(rulePrefix)
                    ) shouldBe expected
                }
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
