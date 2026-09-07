package com.ilynehdev.core.phloem

import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface PhloemFetcher<Dto> {
    suspend fun pullNextPage(): FetchResult

    /** True when the last complete pull finished within the TTL. */
    suspend fun isFresh(): Boolean
}

class PhloemFetcherImpl<Dto>(
    private val model: PhloemModel,
    private val ttl: Duration,
    private val transactor: Transactor,
    private val fetchMetadataStore: FetchMetadataStore,
    private val fetchPage: suspend (cursor: String?) -> FetchPage<Dto>,
    private val persistPage: suspend (items: List<Dto>) -> Unit,
    private val now: () -> Long = System::currentTimeMillis,
) : PhloemFetcher<Dto> {

    private val mutex = Mutex()

    override suspend fun pullNextPage(): FetchResult = mutex.withLock {
        val cursor = fetchMetadataStore.get(model)?.cursor

        val page = try {
            fetchPage(cursor)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return FetchResult.Error(e.toFetchError(), e)
        }

        try {
            transactor.transaction {
                persistPage(page.items)
                fetchMetadataStore.save(
                    model = model,
                    metadata = FetchMetadata(
                        cursor = page.nextCursor,
                        completedAt = if (page.nextCursor == null) now() else null,
                    ),
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return FetchResult.Error(FetchError.StorageError, e)
        }

        FetchResult.Success(hasMore = page.nextCursor != null)
    }

    override suspend fun isFresh(): Boolean {
        val completedAt = fetchMetadataStore.get(model)?.completedAt ?: return false
        return now() - completedAt < ttl.inWholeMilliseconds
    }
}

data class FetchPage<Dto>(val items: List<Dto>, val nextCursor: String?)
