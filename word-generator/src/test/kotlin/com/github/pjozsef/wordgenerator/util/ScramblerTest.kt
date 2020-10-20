package com.github.pjozsef.wordgenerator.util

import io.kotlintest.IsolationMode
import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row
import java.util.*

class ScramblerTest : FreeSpec({
    "scramble" - {
        forall(
            row(
                "scrambles 1 character string",
                "a",
                "a",
                1L
            ),
            row(
                "shuffles input",
                "abcdefgh",
                "hbacgedf",
                100L
            )
        ) { test, input, expected, seed ->
            test {
                Scrambler(Random(seed)).scramble(input) shouldBe expected
            }
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
