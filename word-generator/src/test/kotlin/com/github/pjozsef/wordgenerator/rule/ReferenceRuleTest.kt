package com.github.pjozsef.wordgenerator.rule

import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row

class ReferenceRuleTest: FreeSpec({
    "regex" - {
        forall(
            row(
                "with default prefix",
                ":{ref1}",
                ReferenceRule()
            ),
            row(
                "with custom prefix",
                "custom prefix{ref1}",
                ReferenceRule("custom prefix")
            )
        ) { test, input, rule ->
            test {
                rule.regex.matches(input) shouldBe true
            }
        }
    }
    "evaluate" - {
        val mappings = mapOf(
            "refs" to listOf(
                "ref1=#{rule1}",
                "ref2=constant",
                "ref3=*{rule3}"
            )
        )
        forall(
            row(
                "returns the reference value",
                "ref1",
                "#{rule1}"
            )
        ) { test, rule, expected ->
            test {
                ReferenceRule().evaluate(
                    rule, mappings, mock()
                ) shouldBe expected
            }
        }
    }
}){
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
