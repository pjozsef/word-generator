package com.github.pjozsef.wordgenerator.rule

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.Random

internal class InlineSubstitutionRuleTest : FreeSpec({
    "regex" - {
        forall(
            row(
                "with multiple inline rules",
                "#{rule1|rule2|rule3}",
                InlineSubstitutionRule()
            ),
            row(
                "with custom prefix",
                "custom prefix{rule1|rule2|rule3}",
                InlineSubstitutionRule("custom prefix")
            ),
            row(
                "with multiple rules with whitespace",
                "#{rule1| rule2 | rule3}",
                InlineSubstitutionRule()
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }

        "should not match single value rule" {
            InlineSubstitutionRule().regex.matches("#{rule}") shouldBe false
        }
    }
    "evaluate" - {
        val mappings = mapOf<String, List<String>>()
        val random = mock<Random>()
        forall(
            row(
                "returns corresponding value",
                "a",
                "a",
                0
            ),
            row(
                "returns randomly chosen value",
                "0|1|2|3",
                "3",
                3
            ),
            row(
                "substitution chosen from a composite rule with whitespace",
                "a | b",
                "b",
                1
            ),
            row(
                "ignores degenerate input",
                " a | b|||   \t  |c",
                "b",
                1
            )
        ) { test, rule, expected, randomIndex ->
            test {
                whenever(random.nextInt(any())).doReturn(randomIndex)
                InlineSubstitutionRule().evaluate(
                    rule, mappings, random
                ) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

