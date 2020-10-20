package com.github.pjozsef.wordgenerator.util

import java.util.*

class Scrambler(var random: Random = Random()) {
    fun scramble(input: String) = input.toList().shuffled(random).joinToString("")
}
