package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.Random

class WordGeneratorKtTest : FreeSpec({
    "generateWord" - {
        val random = mock<Random>()
        val mappings = mapOf<String, List<String>>()

        "returns expression as is if it does not contain any rules" {
            generateWord(
                "string constant",
                mappings,
                random,
                SubstitutionRule(),
                "#"
            ) shouldBe "string constant"
        }

        "Substitution" - {
            val substitutionRule = mock<SubstitutionRule> {
                on { evaluate("a", mappings, random) } doReturnConsecutively listOf("x")
                on { evaluate("b", mappings, random) } doReturnConsecutively listOf("y")
                on { evaluate("a+b", mappings, random) } doReturnConsecutively listOf("x", "y")
                on { evaluate("multipleOptions", mappings, random) } doReturnConsecutively listOf("0", "1", "2")
            }

            forall(
                row(
                    "Substitution:substitutes rule to corresponding value",
                    "#{a}",
                    "x",
                    "#"
                ),
                row(
                    "multiple substitutions",
                    "#{a}#{b}",
                    "xy",
                    "#"
                ),
                row(
                    "multiple substitutions with constant values in between",
                    "#{a} - #{b}",
                    "x - y",
                    "#"
                ),
                row(
                    "rule can be anywhere in the expression",
                    "prefix#{a}",
                    "prefixx",
                    "#"
                ),
                row(
                    "rule prefix can be changed",
                    "_!{a}#{notRule}",
                    "_x#{notRule}",
                    "!"
                ),
                row(
                    "rule prefix can be longer than 1 character",
                    "longprefix{a}",
                    "x",
                    "longprefix"
                ),
                row(
                    "substitution chosen from a composite rule",
                    "#{a+b}#{a+b}",
                    "xy",
                    "#"
                )
            ) { test, input, expected, rulePrefix ->
                test {
                    generateWord(
                        input,
                        mappings,
                        random,
                        substitutionRule,
                        rulePrefix
                    ) shouldBe expected
                }
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
