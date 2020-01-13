package com.roguepnz.memeagg.core.model

import org.bson.codecs.pojo.annotations.BsonId

data class Content(@BsonId val id: String?,
                   val sourceId: String,
                   val rawId: String,
                   val contentType: Int,
                   val url: String,
                   val hash: String,
                   val publishTime: Int,
                   val likesCount: Int,
                   val dislikesCount: Int,
                   val commentsCount: Int,
                   val sourcesCount: Int)

