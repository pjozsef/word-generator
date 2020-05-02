package com.github.pjozsef.wordgenerator.web

import com.github.pjozsef.randomtree.io.readTreeFromString
import com.github.pjozsef.wordgenerator.generateWord
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    val server = embeddedServer(Netty, System.getProperty("server.port","8080").toInt()) {
        routing {
            post("/api/generate") {
                val dto = call.receive<GenerateWordDto>()

                val results = generate(dto).also {
                    log.info("Word generation of ${dto.times} word${if(dto.times>1) "s" else ""} took ${it.duration.toLongMilliseconds()} millis.")
                }.value

                call.respond(GenerateWordResponseDto(results, dto.seed))
            }
        }
        install(ContentNegotiation) {
            moshi {
                add(Date::class.java, Rfc3339DateJsonAdapter())
            }
        }
    }
    server.start(wait = true)
}

@OptIn(ExperimentalTime::class)
private fun generate(dto: GenerateWordDto) = with(dto) {
    val random = seed.toLong(36).let(::Random)
    measureTimedValue {
        (1..times).map {
            generateWord(
                expression(dto, random),
                mappings,
                random
            )
        }
    }
}

private fun expression(dto: GenerateWordDto, random: Random) = dto.category?.let {
    readTreeFromString(
        dto.command.replace("\t", "  "),
        { it },
        { it.toSortedMap().values.joinToString(" ") },
        random
    ).getValue(it).value
} ?: dto.command
