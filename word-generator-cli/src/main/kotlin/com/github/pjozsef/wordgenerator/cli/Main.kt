@file:JvmName("Main")

package com.github.pjozsef.wordgenerator.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.pjozsef.randomtree.io.readTreeFromFile
import com.github.pjozsef.wordgenerator.generateWord
import java.io.File
import java.util.*

class MainCommand : CliktCommand() {

    val command: String by argument()
    val randomTreeCategory: String? by argument().optional()

    val contextFolder: File by option("--context", "-c").file(
        exists = true,
        fileOkay = false,
        folderOkay = true,
        readable = true
    ).required()
    val seed: String? by option("--seed", "-s")
    val times: Int by option("--times", "-t").int().default(1)

    override fun run() {
        val mappings = mappings()
        val random = random()

        repeat(times) {
            generateWord(
                expression(random),
                mappings,
                random
            ).let(::println)
        }
    }

    private fun expression(random: Random): String {
        return File(command).takeIf {
            it.exists() && it.isFile
        }?.absolutePath?.let { path ->
            val category = randomTreeCategory
            requireNotNull(category) { "randomTreeCategory must be present when randomTree file is used" }
            readTreeFromFile(
                path,
                { it },
                { it.toSortedMap().values.joinToString(" ") },
                random
            ).getValue(category).value
        } ?: command
    }

    private fun mappings(): Map<String, List<String>> {
        return contextFolder.list { dir, name ->
            name.endsWith(".txt")
        }.map {
            contextFolder.resolve(it)
        }.fold(mapOf()) { acc, file ->
            val key = file.nameWithoutExtension
            val content = file.readLines()
            acc + mapOf(key to content)
        }
    }

    private fun random(): Random {
        return (
                seed?.toLong(36) ?: System.currentTimeMillis()
                    .toString(36)
                    .takeLast(3)
                    .toUpperCase()
                    .also { println("Random seed: $it") }
                    .toLong(36)
                ).let(::Random)
    }

}

fun main(args: Array<String>) = MainCommand().main(args)
