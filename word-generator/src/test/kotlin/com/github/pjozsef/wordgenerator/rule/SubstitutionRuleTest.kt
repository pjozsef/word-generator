package com.github.pjozsef.wordgenerator.rule

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.IsolationMode
import io.kotlintest.assertSoftly
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.Random

class SubstitutionRuleTest : FreeSpec({
    "regex" - {
        forall(
            row(
                "with default prefix",
                "#{rule}",
                SubstitutionRule()
            ),
            row(
                "with custom prefix",
                "custom prefix{rule}",
                SubstitutionRule("custom prefix")
            ),
            row(
                "with multiple rules",
                "custom prefix{rule1+rule2+rule3}",
                SubstitutionRule("custom prefix")
            ),
            row(
                "with multiple rules with whitespace",
                "custom prefix{rule1+ rule2 + rule3}",
                SubstitutionRule("custom prefix")
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }
    }
    "nested rules" - {
        "should only match inner substitution rule" {
            val matches = SubstitutionRule().regex.findAll("#{rule#{embedded}}").toList()
            assertSoftly {
                matches.size shouldBe 1
                matches.first().value shouldBe "#{embedded}"
            }
        }
        "should not match" - {
            forall(
                row(
                    "with embedded reference rule",
                    "#{rule:{embedded}}"
                ),
                row(
                    "with embedded markov rule",
                    "#{rule*{embedded}}"
                ),
                row(
                    "with embedded inline substitution rule",
                    "#{rule#{v1|v2|v3}}"
                )
            ) { test, input ->
                test {
                    SubstitutionRule().regex.findAll(input).toList() shouldBe emptyList()
                }
            }
        }
    }
    "evaluate" - {
        val mappings = mapOf(
            "a" to listOf("x"),
            "b" to listOf("y"),
            "rule with space" to listOf("withspace"),
            "multipleOptions" to listOf("0", "1", "2", "3")
        )
        val random = mock<Random>()
        forall(
            row(
                "returns corresponding value",
                "a",
                "x",
                0
            ),
            row(
                "returns randomly chosen value",
                "multipleOptions",
                "3",
                3
            ),
            row(
                "returns value from rule with spaces",
                "rule with space",
                "withspace",
                0
            ),
            row(
                "substitution chosen from a composite rule",
                "a+b",
                "y",
                1
            ),
            row(
                "substitution chosen from a composite rule with whitespace",
                "a + b",
                "y",
                1
            ),
            row(
                "ignores degenerate input",
                " a + b+++   \t  +",
                "y",
                1
            )
        ) { test, rule, expected, randomIndex ->
            test {
                whenever(random.nextInt(any())).doReturn(randomIndex)
                SubstitutionRule().evaluate(
                    rule, mappings, random
                ) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
