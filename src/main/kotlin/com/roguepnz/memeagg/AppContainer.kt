package com.roguepnz.memeagg

import com.roguepnz.memeagg.api.FeedController
import com.roguepnz.memeagg.api.HelloController
import com.roguepnz.memeagg.crawler.ContentCrawler
import com.roguepnz.memeagg.db.MongoDbBuilder
import com.roguepnz.memeagg.http.HttpClientBuilder
import com.roguepnz.memeagg.source.ContentSourceBuilder
import io.ktor.client.HttpClient
import org.litote.kmongo.coroutine.CoroutineDatabase
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

    init {
        put(FeedController())
        put(HelloController())
        put(HttpClientBuilder.build())
        put(MongoDbBuilder.build())

        put(ContentSourceBuilder(Config.sources, get(HttpClient::class), get(CoroutineDatabase::class)))
        put(ContentCrawler(Config.crawler, get(ContentSourceBuilder::class)))
    }
}