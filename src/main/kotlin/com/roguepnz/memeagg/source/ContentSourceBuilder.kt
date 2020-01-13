package com.roguepnz.memeagg.source

import com.roguepnz.memeagg.source.config.ContentSourceConfig
import com.roguepnz.memeagg.source.config.SourceType
import com.roguepnz.memeagg.source.cursor.CursorState
import com.roguepnz.memeagg.source.ngag.tag.NGagTagContentSource
import com.roguepnz.memeagg.source.ngag.tag.NGagTagConfig
import com.roguepnz.memeagg.source.ngag.NGagClient
import com.roguepnz.memeagg.source.ngag.group.NGagGroupConfig
import com.roguepnz.memeagg.source.ngag.group.NGagGroupContentSource
import com.roguepnz.memeagg.source.state.DbStateProvider
import com.typesafe.config.Config
import io.ktor.client.HttpClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.broadcast
import org.litote.kmongo.coroutine.CoroutineDatabase

class ContentSourceBuilder(config: Config, private val httpClient: HttpClient, private val db: CoroutineDatabase) {

    private val configs: Map<String, ContentSourceConfig> = readConfig(config)

    private fun readConfig(c: Config): Map<String, ContentSourceConfig> {
        return c.getConfigList("sources")
            .asSequence()
            .map { ContentSourceConfig(it) }
            .map { Pair(it.id, it) }
            .toMap()
    }

    fun build(id: String): ContentSource {
        return build(configs[id] ?: error("config not found for source: $id"))
    }

    private fun build(config: ContentSourceConfig): ContentSource {
        return when(config.type) {
            SourceType.NGAG_TAG -> NGagTagContentSource(NGagTagConfig(config.config),
                NGagClient(httpClient), DbStateProvider(db, config.id, CursorState::class))

            SourceType.NGAG_GROUP -> NGagGroupContentSource(NGagGroupConfig(config.config),
                NGagClient(httpClient), DbStateProvider(db, config.id, CursorState::class))


            else -> throw IllegalArgumentException("unsupported source type: " + config.type)
        }
    }

    val sources: List<String> get() = configs.keys.toList()
}