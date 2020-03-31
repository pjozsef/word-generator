package com.github.pjozsef.wordgenerator.web

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateWordDto(
    val command: String,
    val mappings: Map<String, List<String>>,
    val times: Int = 1,
    val category: String? = null,
    val seed: String = random()
)

@JsonClass(generateAdapter = true)
data class GenerateWordResponseDto(
    val results: List<String>,
    val seed: String
)

private fun random() = System.currentTimeMillis()
    .toString(36)
    .takeLast(3)
    .toUpperCase()
