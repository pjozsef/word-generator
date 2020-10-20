package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.wordgenerator.util.Scrambler
import com.nhaarman.mockitokotlin2.*
import io.kotlintest.IsolationMode
import io.kotlintest.assertSoftly
import io.kotlintest.data.suspend.forall
import io.kotlintest.matchers.numerics.shouldBeInRange
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.*

class ScrambleRuleTest : FreeSpec({
    "regex" - {
        forall(
            row(
                "matches anything within the brackets",
                "!{inside to scramble}",
                ScrambleRule()
            ),
            row(
                "matches custom prefix",
                "custom prefix{inside to scramble}",
                ScrambleRule("custom prefix")
            ),
            row(
                "matches multiple options",
                "!{option1|o2|asdf}",
                ScrambleRule()
            ),
            row(
                "matches multiple options with whitespace",
                "!{option1 |   o2 |   asdf  }",
                ScrambleRule()
            ),
            row(
                "matches range",
                "!{option1|option2,4-8}",
                ScrambleRule()
            ),
            row(
                "matches range with multiple digits",
                "!{option1|option2,14-890}",
                ScrambleRule()
            ),
            row(
                "matches range with multiple digits with whitespaces",
                "!{option1|option2, 14 - 890 }",
                ScrambleRule()
            ),
            row(
                "matches range with range minimum only",
                "!{option1|option2,1-}",
                ScrambleRule()
            ),
            row(
                "matches range with range maximum only",
                "!{option1|option2,-30}",
                ScrambleRule()
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }
    }
    "nested rules" - {
        "should only match inner scramble rule" {
            val matches = ScrambleRule().regex.findAll("!{v1|v2|!{inner1|inner2}}").toList()
            assertSoftly {
                matches.size shouldBe 1
                matches.first().value shouldBe "!{inner1|inner2}"
            }
        }
        "should not match" - {
            forall(
                row(
                    "with embedded reference rule",
                    "!{v1|v2|:{embedded}}"
                ),
                row(
                    "with embedded substitution rule",
                    "!{v1|v2|#{embedded}}"
                ),
                row(
                    "with embedded markov rule",
                    "!{v1|v2|*{embedded}}"
                )
            ) { test, input ->
                test {
                    InlineSubstitutionRule().regex.findAll(input).toList() shouldBe emptyList()
                }
            }
        }
    }
    "evaluate" - {
        val mappings = mapOf(
            "mapping1" to listOf("zqwertyy"),
            "mapping2" to listOf("12", "34", "56")
        )
        val random = mock<Random>()
        val scrambler = mock<Scrambler>()
        forall(
            row(
                "returns single value",
                "a",
                "a",
                listOf(0)
            ),
            row(
                "scrambles single word",
                "abc",
                "abc",
                listOf(0)
            ),
            row(
                "scrambles a word",
                "aaa | abc | efg",
                "abc",
                listOf(1)
            ),
            row(
                "scrambles a word and removes whitespace",
                "aaa | a b c | efg",
                "abc",
                listOf(1)
            ),
            row(
                "chooses from mapping if finds corresponding entry",
                "mapping2 | abc | efg",
                "56",
                listOf(0, 2)
            )
        ) { test, rule, chosenInput, randomInts ->
            test {
                val expected = "scrambled word"
                whenever(scrambler.scramble(chosenInput)) doReturn expected
                whenever(random.nextInt(any())) doReturnConsecutively randomInts

                ScrambleRule(scrambler = scrambler).evaluate(
                    rule, mappings, random
                ) shouldBe expected
            }
        }
        "always sets random on scrambler" {
            whenever(scrambler.scramble(any())) doReturn "abc"

            ScrambleRule(scrambler = scrambler).evaluate(
                "aaa", mappings, random
            )

            verify(scrambler).random = random
        }

        "with range" - {
            forall(
                row(
                    "with an inline word",
                    "abcdefgh, 1-3",
                    1..3
                ),
                row(
                    "with an inline word, only min range",
                    "abcdefgh, 2-",
                    2..8
                ),
                row(
                    "with an inline word, only max range",
                    "abcdefgh, -7",
                    1..7
                ),
                row(
                    "with a mapping",
                    "mapping1, 1-3",
                    1..3
                ),
                row(
                    "with a mapping, only min range",
                    "mapping1, 2-",
                    2..8
                ),
                row(
                    "with a mapping, only max range",
                    "mapping1, -7",
                    1..7
                )
            ) { test, rule, range ->
                test {
                    (1..1000).map {
                        ScrambleRule().evaluate(rule, mappings, random)
                    }.forEach {
                        it.length shouldBeInRange range
                    }
                }
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
