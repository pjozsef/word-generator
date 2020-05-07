package com.github.pjozsef.wordgenerator.cache

import com.github.benmanes.caffeine.cache.Caffeine
import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class InMemoryCacheTest: FreeSpec({
    "InMemoryCache" - {
        val caffeineCache = Caffeine.newBuilder().build<String, String>()
        val inMemoryCache = InMemoryCache(caffeineCache)

        "delegates set call to cache" {
            inMemoryCache["key"] = "value"

            caffeineCache.getIfPresent("key") shouldBe "value"
        }

        "delegates get call to cache" {
            caffeineCache.put("key", "value")

            inMemoryCache["key"] shouldBe "value"
        }
    }
}){
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
