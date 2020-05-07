package com.github.pjozsef.wordgenerator.rule

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.pjozsef.markovchain.MarkovChain
import com.github.pjozsef.markovchain.Transition
import com.github.pjozsef.markovchain.constraint.Constraints
import com.github.pjozsef.markovchain.util.TransitionRule
import com.github.pjozsef.markovchain.util.asDice
import com.github.pjozsef.wordgenerator.cache.InMemoryCache
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
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
    val random = mock<Random>()
    val mappings = mapOf(
        "rule" to listOf("x", "y", "xy", "yy"),
        "rule2" to listOf("x2", "y2", "xy2", "yy2")
    )
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
    "nested rules" - {
        "should only match inner markov rule" {
            val matches = MarkovRule().regex.findAll("*{rule*{embedded}}").toList()
            assertSoftly {
                matches.size shouldBe 1
                matches.first().value shouldBe "*{embedded}"
            }
        }
        "should not match" - {
            forall(
                row(
                    "with embedded reference rule",
                    "*{rule:{embedded}}"
                ),
                row(
                    "with embedded substitution rule",
                    "*{rule#{embedded}}"
                ),
                row(
                    "with embedded inline substitution rule",
                    "*{rule#{v1|v2|v3}}"
                )
            ) { test, input ->
                test {
                    MarkovRule().regex.findAll(input).toList() shouldBe emptyList()
                }
            }
        }
    }
    "uses cache" - {
        val cache = InMemoryCache<OrderAndWords, Transition>(Caffeine.newBuilder().build())
        val rule = MarkovRule(cache = cache)

        "gets transition from cache" {
            val cachedResult = "a"
            val cachedTransition = TransitionRule.fromWords(listOf(cachedResult), 1, "#").asDice(random)
            cache[1 to mappings.getValue("rule")] = cachedTransition
            rule.evaluate("rule", mappings, random) shouldBe cachedResult
        }

        "sets transition in cache" {
            rule.evaluate("rule#3", mappings, random)

            val words = mappings.getValue("rule")
            cache[3 to words] shouldBe TransitionRule.fromWords(words, 3, "#").asDice(random)
        }

        "caches depend on markov order" {
            rule.evaluate("rule#3", mappings, random)
            rule.evaluate("rule#2", mappings, random)
            rule.evaluate("rule#1", mappings, random)

            val expectedTransitions = (1..3).map { TransitionRule.fromWords(mappings.getValue("rule"), it, "#").asDice(random) }
            val actualTransitions = (1..3).map { cache[it to mappings.getValue("rule")] }

            actualTransitions shouldBe expectedTransitions
        }
    }
    "evaluate" - {
        "when no result" - {
            "returns constant" {
                val factory = { _: Transition, _: String, _: Int ->
                    mock<MarkovChain> {
                        on { generate(any(), any(), any()) } doReturn listOf()
                    }
                }
                MarkovRule(markovChainFactory = factory)
                    .evaluate(
                        "rule",
                        mapOf("rule" to listOf("a")),
                        random
                    ) shouldBe "_NO_RESULT_"
            }
        }

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
