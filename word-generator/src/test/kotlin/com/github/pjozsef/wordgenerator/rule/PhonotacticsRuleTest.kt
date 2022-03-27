package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.randomtree.io.RandomTreeReader
import com.github.pjozsef.randomtree.io.readTreeFromMap
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.assertSoftly
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.Random

class PhonotacticsRuleTest : FreeSpec({
    "regex" - {
        forall(
            row(
                "matches anything within the brackets",
                "@{inside to scramble}",
                PhonotacticsRule()
            ),
            row(
                "matches custom prefix",
                "custom prefix{inside to scramble}",
                PhonotacticsRule("custom prefix")
            ),
            row(
                "matches anything within the brackets with whitespace",
                "@{  option1    o2    asdf  }",
                PhonotacticsRule()
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }
    }
    "nested rules" - {
        "should only match inner phonotactics rule" {
            val matches = PhonotacticsRule().regex.findAll("@{v1@{inner1}}").toList()
            assertSoftly {
                matches.size shouldBe 1
                matches.first().value shouldBe "@{inner1}"
            }
        }
        "should not match" - {
            forall(
                row(
                    "with embedded reference rule",
                    "@{v1v2:{embedded}}"
                ),
                row(
                    "with embedded substitution rule",
                    "@{v1v2|#{embedded}}"
                ),
                row(
                    "with embedded markov rule",
                    "@{v1v2*{embedded}}"
                )
            ) { test, input ->
                test {
                    PhonotacticsRule().regex.findAll(input).toList() shouldBe emptyList()
                }
            }
        }
    }
    "evaluate" - {
        val mappings = mapOf(
            "START" to listOf("a#")
        )
        val random = mock<Random>()

        val trees = readTreeFromMap(mappings, RandomTreeReader.IDENTITY_MAPPER, RandomTreeReader.CONCAT_COMBINER(" "), random)
        forall(
            row("returns single value", "START", "a")
        ) { test, startNode, expected ->
            PhonotacticsRule().eval(
                startNode, trees
            ) shouldBe expected
        }
//        RandomTree
    }
//    "evaluate" - {
//        val mappings = mapOf(
//            "mapping1" to listOf("zqwertyy"),
//            "mapping2" to listOf("12", "34", "56")
//        )
//        val random = mock<Random>()
//        val scrambler = mock<Scrambler>()
//        forall(
//            row(
//                "returns single value",
//                "a",
//                "a",
//                listOf(0)
//            ),
//            row(
//                "scrambles single word",
//                "abc",
//                "abc",
//                listOf(0)
//            ),
//            row(
//                "scrambles a word",
//                "aaa | abc | efg",
//                "abc",
//                listOf(1)
//            ),
//            row(
//                "scrambles a word and removes whitespace",
//                "aaa | a b c | efg",
//                "abc",
//                listOf(1)
//            ),
//            row(
//                "chooses from mapping if finds corresponding entry",
//                "mapping2 | abc | efg",
//                "56",
//                listOf(0, 2)
//            )
//        ) { test, rule, chosenInput, randomInts ->
//            test {
//                val expected = "scrambled word"
//                whenever(scrambler.scramble(chosenInput)) doReturn expected
//                whenever(random.nextInt(any())) doReturnConsecutively randomInts
//
//                ScrambleRule(scrambler = scrambler).evaluate(
//                    rule, mappings, random
//                ) shouldBe expected
//            }
//        }
//        "always sets random on scrambler" {
//            whenever(scrambler.scramble(any())) doReturn "abc"
//
//            ScrambleRule(scrambler = scrambler).evaluate(
//                "aaa", mappings, random
//            )
//
//            verify(scrambler).random = random
//        }
//
//        "with range" - {
//            forall(
//                row(
//                    "with an inline word",
//                    "abcdefgh, 1-3",
//                    1..3
//                ),
//                row(
//                    "with an inline word, only min range",
//                    "abcdefgh, 2-",
//                    2..8
//                ),
//                row(
//                    "with an inline word, only max range",
//                    "abcdefgh, -7",
//                    1..7
//                ),
//                row(
//                    "with a mapping",
//                    "mapping1, 1-3",
//                    1..3
//                ),
//                row(
//                    "with a mapping, only min range",
//                    "mapping1, 2-",
//                    2..8
//                ),
//                row(
//                    "with a mapping, only max range",
//                    "mapping1, -7",
//                    1..7
//                )
//            ) { test, rule, range ->
//                test {
//                    (1..1000).map {
//                        ScrambleRule().evaluate(rule, mappings, random)
//                    }.forEach {
//                        it.length shouldBeInRange range
//                    }
//                }
//            }
//        }
//
//        "with phonetics ratio" - {
//            forall(
//                row(0.5),
//                row(0.1),
//                row(0.1555),
//                row(0.9),
//                row(0.0),
//                row(1.0)
//            ) { distribution ->
//                "distribution matches ratio of $distribution" {
//                    val sampleSize = 10_000
//                    val delta = 0.015
//                    val vowels = generateSequence { "a" }.take(sampleSize).joinToString("")
//                    val consonants = generateSequence { "b" }.take(sampleSize).joinToString("")
//                    val input = vowels + consonants
//
//                    val result = ScrambleRule()
//                        .evaluate("$input, $distribution", mappings, Random())
//                        .take(sampleSize).toList()
//                    val vowelRatio = result.count { it == 'a' }.toDouble() / sampleSize
//                    val consonantRatio = result.count { it == 'b' }.toDouble() / sampleSize
//
//                    assertSoftly {
//                        vowelRatio shouldBe distribution.plusOrMinus(delta)
//                        consonantRatio shouldBe (1 - distribution).plusOrMinus(delta)
//                    }
//                }
//
//            }
//        }
//    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
