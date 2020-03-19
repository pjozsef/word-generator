package com.github.pjozsef.wordgenerator.rule

import java.util.*

interface Rule {
    val regex: Regex

    fun evaluate(
        rule: String,
        mappings: Map<String, List<String>>,
        random: Random
    ): String
}
