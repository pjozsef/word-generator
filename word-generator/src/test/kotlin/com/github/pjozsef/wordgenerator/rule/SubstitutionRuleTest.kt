package com.github.pjozsef.wordgenerator.rule

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

class SubstitutionRuleTest : FreeSpec({
    "evaluate" - {
        val mappings = mapOf(
            "a" to listOf("x"),
            "b" to listOf("y"),
            "multipleOptions" to listOf("0", "1", "2", "3")
        )
        val random = mock<Random>()
        forall(
            row(
                "returns corresponding value",
                "a",
                "x",
                listOf(0)
            ),
            row(
                "returns randomly chosen value",
                "multipleOptions",
                "3",
                listOf(3)
            ),
            row(
                "substitution chosen from a composite rule",
                "a+b",
                "y",
                listOf(1)
            )
        ) { test, rule, expected, randomIndices ->
            test {
                whenever(random.nextInt(any())).doReturnConsecutively(randomIndices)
                SubstitutionRule().evaluate(
                    rule, mappings, random
                ) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
