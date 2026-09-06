package com.ilynehdev.core.phloem

data class FetchMetadata(
    val cursor: String?,        // next page to fetch; null once catalog fully crawled
    val completedAt: Long?,     // epoch millis of last complete pass; null = never finished
)

interface FetchMetadataStore {
    suspend fun get(model: PhloemModel): FetchMetadata?      // null = never fetched
    suspend fun save(model: PhloemModel, metadata: FetchMetadata)
}