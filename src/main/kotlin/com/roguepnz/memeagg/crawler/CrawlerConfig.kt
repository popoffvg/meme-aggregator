package com.roguepnz.memeagg.crawler

import com.typesafe.config.Config

class CrawlerConfig(config: Config) {
    val workersCount: Int = config.getInt("workersCount")
    val writerQueueSize: Int = config.getInt("writerQueueSize")
    val writerWaitTimeSec: Int = config.getInt("writerWaitTimeSec")
}