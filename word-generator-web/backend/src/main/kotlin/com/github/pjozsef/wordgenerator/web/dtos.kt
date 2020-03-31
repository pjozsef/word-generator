package com.github.pjozsef.wordgenerator.web

data class GenerateWordDto(
    val command: String,
    val mappings: Map<String, List<String>>,
    val times: Int = 1,
    val category: String? = null,
    val seed: String = random()
)

private fun random() = System.currentTimeMillis()
    .toString(36)
    .takeLast(3)
    .toUpperCase()
