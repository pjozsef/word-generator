package com.github.pjozsef.wordgenerator.cache

import com.github.benmanes.caffeine.cache.Cache as CaffeineCache

class InMemoryCache<K: Any,V>(val caffeineCache: CaffeineCache<K,V>): Cache<K,V> {
    override fun get(key: K) = caffeineCache.getIfPresent(key)

    override fun set(key: K, value: V) = caffeineCache.put(key, value)
}
