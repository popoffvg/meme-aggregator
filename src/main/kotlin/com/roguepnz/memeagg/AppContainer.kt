package com.roguepnz.memeagg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.roguepnz.memeagg.api.FeedController
import com.roguepnz.memeagg.api.HelloController
import com.roguepnz.memeagg.crawler.ContentCrawler
import com.roguepnz.memeagg.source.ContentSourceLoader
import com.roguepnz.memeagg.source.ngag.NGagContentSource
import com.roguepnz.memeagg.source.ngag.NGagSourceConfig
import com.roguepnz.memeagg.source.ngag.api.NGagClient
import com.roguepnz.memeagg.source.reddit.RedditMemeSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import kotlin.reflect.KClass
import kotlin.reflect.full.cast


object AppContainer {
    private val components: MutableList<Any> = ArrayList()

    fun <T : Any> get(type: KClass<T>): T {
        val list = getAll(type)
        if (list.isEmpty()) {
            throw IllegalArgumentException()
        }
        return list[0]
    }

    fun <T : Any> getAll(type: KClass<T>): List<T> {
        return components.asSequence()
            .filter { type.isInstance(it) }
            .map { type.cast(it) }
            .toList()
    }

    private fun put(component: Any) {
        components.add(component)
    }

    private fun put(initFn: () -> Any) {
        components.add(initFn())
    }

    init {
        put(FeedController())
        put(HelloController())
        put {
            HttpClient(Apache) {
                install(JsonFeature) {
                    serializer = JacksonSerializer {
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                    }
                }
            }
        }

        put(ContentSourceLoader(get(HttpClient::class)))

        put(ContentCrawler(get(ContentSourceLoader::class)))
    }
}