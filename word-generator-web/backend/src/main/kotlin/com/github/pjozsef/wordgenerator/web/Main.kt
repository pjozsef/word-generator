package com.github.pjozsef.wordgenerator.web

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.pjozsef.markovchain.Transition
import com.github.pjozsef.randomtree.io.readTreeFromString
import com.github.pjozsef.wordgenerator.cache.InMemoryCache
import com.github.pjozsef.wordgenerator.generateWord
import com.github.pjozsef.wordgenerator.rule.InlineSubstitutionRule
import com.github.pjozsef.wordgenerator.rule.MarkovRule
import com.github.pjozsef.wordgenerator.rule.OrderAndWords
import com.github.pjozsef.wordgenerator.rule.ReferenceRule
import com.github.pjozsef.wordgenerator.rule.SubstitutionRule
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.measureTimedValue

val cache = InMemoryCache<OrderAndWords, Transition>(Caffeine.newBuilder()
    .maximumSize(20)
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .build())

fun main() {
    val server = embeddedServer(Netty, System.getProperty("server.port", "8080").toInt()) {
        routing {
            post("/api/generate") {
                val dto = call.receive<GenerateWordDto>()

                val results = generate(dto).also {
                    log.info("Word generation of ${dto.times} word${if (dto.times > 1) "s" else ""} took ${it.duration.toLongMilliseconds()} millis.")
                }.value.map(String::trim).filter(String::isNotBlank)

                call.respond(GenerateWordResponseDto(results, dto.seed))
            }
        }
        install(ContentNegotiation) {
            moshi {
                add(Date::class.java, Rfc3339DateJsonAdapter())
            }
        }
        install(CallLogging) {
            level = Level.INFO
        }
    }
    server.start(wait = true)
}

private fun generate(dto: GenerateWordDto) = with(dto) {
    val random = seed.toLong(36).let(::Random)
    measureTimedValue {
        (1..times).map {
            generateWord(
                expression(dto, random),
                mappings,
                random,
                listOf(
                    SubstitutionRule(),
                    InlineSubstitutionRule(),
                    MarkovRule(cache = cache),
                    ReferenceRule()
                )
            )
        }
    }
}

private fun expression(dto: GenerateWordDto, random: Random) = dto.rootExpression?.let { root ->
    readTreeFromString(
        dto.expression.replace("\t", "  "),
        { it },
        { it.toSortedMap().values.joinToString(" ") },
        random
    ).getValue(root).value
} ?: dto.expression
