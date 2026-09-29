package com.ilynehdev.core.phloem.pagefetcher

import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.toFetchError
import com.ilynehdev.core.phloem.Transactor
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Resumable pull of a paged collection, one page per [pullNextPage] call.
 *
 * Each page is persisted in the same transaction as its next cursor, so an interrupted pull
 * resumes where it stopped, even across process restarts. `completedAt` is stamped only when
 * the last page lands, so an incomplete pass always resumes rather than restarting, and the
 * TTL clock starts only after a complete pass. Pulls are serialized, so concurrent callers
 * never fetch the same page twice.
 */
interface PhloemPageFetcher<Dto> {
    suspend fun pullNextPage(): FetchResult

    /** True when the last complete pull finished within the TTL. */
    suspend fun isFresh(): Boolean
}

class PhloemPageFetcherImpl<Dto>(
    private val model: PhloemModel,
    private val freshness: Freshness,
    private val transactor: Transactor,
    private val fetchMetadataStore: FetchMetadataStore,
    private val fetchPage: suspend (cursor: String?) -> FetchPage<Dto>,
    private val persistPage: suspend (items: List<Dto>) -> Unit,
) : PhloemPageFetcher<Dto> {

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
                        completedAt = if (page.nextCursor == null) freshness.newTimestamp() else null,
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
        return freshness.isFresh(fetchMetadataStore.get(model)?.completedAt)
    }
}

data class FetchPage<Dto>(val items: List<Dto>, val nextCursor: String?)
