package com.github.pjozsef.wordgenerator

import com.github.pjozsef.wordgenerator.rule.InlineSubstitutionRule
import com.github.pjozsef.wordgenerator.rule.MarkovRule
import com.github.pjozsef.wordgenerator.rule.ReferenceRule
import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.lang.IllegalStateException
import java.time.Duration
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
                listOf(SubstitutionRule())
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
                        listOf(SubstitutionRule(rulePrefix))
                    ) shouldBe expected
                }
            }
        }
        "multiple rules with recursion" {
            val substitution = mock<SubstitutionRule> {
                on { evaluate("a", mappings, random) }.thenReturn("substitution")
                on { regex }.thenReturn(SubstitutionRule().regex)
            }
            val inlineSubstitution = mock<InlineSubstitutionRule> {
                on { evaluate("b|c|d", mappings, random) }.thenReturn("inlineSubstitution")
                on { regex }.thenReturn(InlineSubstitutionRule().regex)
            }
            val markov = mock<MarkovRule> {
                on { evaluate("markov#2, 4-5", mappings, random) }.thenReturn("markov1")
                on { evaluate("markov3#10", mappings, random) }.thenReturn("markov2")
                on { regex }.thenReturn(MarkovRule().regex)
            }
            val reference = mock<ReferenceRule> {
                on { evaluate("ref1", mappings, random) }.thenReturn("*{markov3#10}")
                on { evaluate("ref2", mappings, random) }.thenReturn(":{ref3}")
                on { evaluate("ref3", mappings, random) }.thenReturn("deepreference")
                on { regex }.thenReturn(ReferenceRule().regex)
            }

            val actual = generateWord(
                ":{ref1}_#{b|c|d}_*{markov#2, 4-5}_#{a}__:{ref2}",
                mappings,
                random,
                listOf(substitution, inlineSubstitution, markov, reference)
            )

            actual shouldBe "markov2_inlineSubstitution_markov1_substitution__deepreference"
        }

        "recursion with infinit loop times out".config(timeout = Duration.ofSeconds(1)) {
            val mappings = mapOf(
                "refs" to listOf(
                    "ref1=:{ref2}",
                    "ref2=:{ref3}",
                    "ref3=:{ref1}"
                )
            )
            shouldThrow<IllegalStateException> {
                generateWord(":{ref1}", mappings, mock(), listOf(ReferenceRule()))
            }.message shouldBe "Reached maximum depth of recursion: 100"
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
