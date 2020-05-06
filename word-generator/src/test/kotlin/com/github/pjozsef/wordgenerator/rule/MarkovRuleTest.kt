package com.github.pjozsef.wordgenerator.rule

import com.github.pjozsef.markovchain.MarkovChain
import com.github.pjozsef.markovchain.Transition
import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.asDice
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.assertSoftly
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.*

class MarkovRuleTest : FreeSpec({
    "regex matches" - {
        forall(
            row(
                "with default prefix",
                "*{rule}",
                MarkovRule()
            ),
            row(
                "with order",
                "*{rule#3}",
                MarkovRule()
            ),
            row(
                "with multiple rules",
                "*{rule1+rule2}",
                MarkovRule()
            ),
            row(
                "with multiple rules and order",
                "*{rule1+rule2#3}",
                MarkovRule()
            ),
            row(
                "with multiple rules and order and extra whitespace",
                "*{ rule1 +   rule2 #3}",
                MarkovRule()
            ),
            row(
                "with custom prefix",
                "custom prefix{rule}",
                MarkovRule("custom prefix")
            ),
            row(
                "with constraints",
                "*{rule, -3, prefix*inside*!end, notThis|notThat, !hybrid}",
                MarkovRule()
            ),
            row(
                "with order and constraints",
                "*{rule#5, -3, prefix*inside*!end, notThis|notThat, !hybrid}",
                MarkovRule()
            ),
            row(
                "with degenerate constraints",
                "*{rule1, 5-7     , , a*b*c}",
                MarkovRule()
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }
    }
    "evaluate" - {
        val mappings = mapOf(
            "rule" to listOf("x", "y", "xy", "yy"),
            "rule2" to listOf("x2", "y2", "xy2", "yy2")
        )
        val random = mock<Random>()
        forall(
            row(
                "with simple rule",
                "rule",
                1,
                mappings.getValue("rule"),
                Constraints()
            ),
            row(
                "with order",
                "rule#4",
                4,
                mappings.getValue("rule"),
                Constraints()
            ),
            row(
                "with constraints",
                "rule, 4-50, !hybrid, a**b",
                1,
                mappings.getValue("rule"),
                Constraints(
                    minLength = 4,
                    maxLength = 50,
                    startsWith = "a",
                    endsWith = "b",
                    hybridPrefixPostfix = false
                )
            ),
            row(
                "with order and constraints",
                "rule#6, 4-50, !hybrid, a**b",
                6,
                mappings.getValue("rule"),
                Constraints(
                    minLength = 4,
                    maxLength = 50,
                    startsWith = "a",
                    endsWith = "b",
                    hybridPrefixPostfix = false
                )
            ),
            row(
                "ignores degenerate input",
                "rule, 4-50,,    ,,   !hybrid, a**b",
                1,
                mappings.getValue("rule"),
                Constraints(
                    minLength = 4,
                    maxLength = 50,
                    startsWith = "a",
                    endsWith = "b",
                    hybridPrefixPostfix = false
                )
            ),
            row(
                "respects multiple inputs",
                " rule +rule2",
                1,
                mappings.getValue("rule") + mappings.getValue("rule2"),
                Constraints()
            )
        ) { test, input, order, words, constraints ->
            test {
                val success = listOf("success")
                val markovChain = mock<MarkovChain> {
                    on { generate(order, 1, constraints) }.thenReturn(success)
                }
                val factory = mock<(Transition, String, Int) -> MarkovChain> {
                    val transition = TransitionRule.fromWords(words, order, "#").asDice(random)
                    on { invoke(eq(transition), eq("#"), eq(1_000_000)) }.thenReturn(markovChain)
                }
                MarkovRule(markovChainFactory = factory).evaluate(input, mappings, random) shouldBe success.first()
            }
        }
    }
    "markovChain created by rule generates correct output" {
        val seed = 234543234L
        val ruleRandom = Random(seed)
        val chainRandom = Random(seed)

        val mappings = mapOf("markovRule" to words)
        val rule = "markovRule#4, 4-10, *i*"

        val markovChain = MarkovChain(TransitionRule.fromWords(words, 4).asDice(chainRandom))
        val constraints = Constraints(
            minLength = 4,
            maxLength = 10,
            contains = listOf("i")
        )

        val markovRule = MarkovRule()

        assertSoftly {
            repeat(500) {
                val expected = markovChain.generate(4, 1, constraints).toList().let {
                    it[chainRandom.nextInt(it.size)]
                }
                val actual = markovRule.evaluate(rule, mappings, ruleRandom)

                actual shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private val words = listOf(
    "lorem",
    "ipsum",
    "dolor",
    "sit",
    "amet",
    "consectetur",
    "adipiscing",
    "elit",
    "sed",
    "do",
    "eiusmod",
    "tempor",
    "incididunt",
    "ut",
    "labore",
    "et",
    "dolore",
    "magna",
    "aliqua"
)
