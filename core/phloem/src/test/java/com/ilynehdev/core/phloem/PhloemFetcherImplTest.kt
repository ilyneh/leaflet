package com.ilynehdev.core.phloem

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.io.IOException
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhloemFetcherImplTest {

    private class FakeMetadataStore : FetchMetadataStore {
        val map = mutableMapOf<PhloemModel, FetchMetadata>()
        override suspend fun get(model: PhloemModel) = map[model]
        override suspend fun save(model: PhloemModel, metadata: FetchMetadata) {
            map[model] = metadata
        }
    }

    private val store = FakeMetadataStore()
    private val persisted = mutableListOf<String>()
    private val requestedCursors = mutableListOf<String?>()
    private var currentTime = 1_000_000L

    /** pages: cursor (null = first) -> FetchPage returned for it */
    private fun fetcher(
        pages: Map<String?, FetchPage<String>> = emptyMap(),
        fetchPage: (suspend (String?) -> FetchPage<String>)? = null,
        persistPage: (suspend (List<String>) -> Unit)? = null,
    ) = PhloemFetcherImpl(
        model = PhloemModel.PlantCatalog,
        ttl = 24.hours,
        transactor = { it() },
        fetchMetadataStore = store,
        fetchPage = fetchPage ?: { cursor ->
            requestedCursors += cursor
            pages.getValue(cursor)
        },
        persistPage = persistPage ?: { items -> persisted += items },
        now = { currentTime },
    )

    // ---- crawl progression ----
    @Test
    fun `first pull starts with null cursor and persists page`() = runTest {
        val fetcher = fetcher(pages = mapOf(null to FetchPage(listOf("a", "b"), nextCursor = "2")))

        val result = fetcher.pullNextPage()

        assertEquals(listOf<String?>(null), requestedCursors)
        assertEquals(listOf("a", "b"), persisted)
        assertEquals(FetchResult.Success(hasMore = true), result)
        assertEquals("2", store.map[PhloemModel.PlantCatalog]?.cursor)
        assertNull(store.map[PhloemModel.PlantCatalog]?.completedAt)
    }

    @Test
    fun `resumes from stored cursor`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = "3", completedAt = null)
        val fetcher = fetcher(pages = mapOf("3" to FetchPage(listOf("c"), nextCursor = "4")))

        fetcher.pullNextPage()

        assertEquals(listOf<String?>("3"), requestedCursors)
        assertEquals("4", store.map[PhloemModel.PlantCatalog]?.cursor)
    }

    @Test
    fun `last page clears cursor and stamps completedAt`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = "9", completedAt = null)
        val fetcher = fetcher(pages = mapOf("9" to FetchPage(listOf("z"), nextCursor = null)))

        val result = fetcher.pullNextPage()

        assertEquals(FetchResult.Success(hasMore = false), result)
        val meta = store.map[PhloemModel.PlantCatalog]
        assertNull(meta?.cursor)
        assertEquals(currentTime, meta?.completedAt)
    }

    @Test
    fun `completed catalog restarts from first page`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = null, completedAt = 1L)
        val fetcher = fetcher(pages = mapOf(null to FetchPage(listOf("a"), nextCursor = "2")))

        fetcher.pullNextPage()

        assertEquals(listOf<String?>(null), requestedCursors)
        val meta = store.map[PhloemModel.PlantCatalog]
        assertEquals("2", meta?.cursor)
        assertNull(meta?.completedAt)   // recrawl in progress re-arms the TTL gate
    }

    // ---- error classification ----
    @Test
    fun `fetch failure leaves cursor untouched and persists nothing`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = "5", completedAt = null)
        val boom = RuntimeException("boom")
        val fetcher = fetcher(fetchPage = { throw boom })

        val result = fetcher.pullNextPage()

        assertEquals(FetchResult.Error(FetchError.General, boom), result)
        assertTrue(persisted.isEmpty())
        assertEquals("5", store.map[PhloemModel.PlantCatalog]?.cursor)
    }

    @Test
    fun `IOException classifies as Offline`() = runTest {
        val fetcher = fetcher(fetchPage = { throw IOException("no network") })

        val result = fetcher.pullNextPage() as FetchResult.Error

        assertEquals(FetchError.Offline, result.error)
    }

    @Test
    fun `ktor statuses classify as Forbidden, RateLimited, ServerDown`() = runTest {
        assertEquals(FetchError.Forbidden, errorFor(HttpStatusCode.Forbidden))
        assertEquals(FetchError.Forbidden, errorFor(HttpStatusCode.Unauthorized))
        assertEquals(FetchError.RateLimited, errorFor(HttpStatusCode.TooManyRequests))
        assertEquals(FetchError.ServerDown, errorFor(HttpStatusCode.InternalServerError))
        assertEquals(FetchError.General, errorFor(HttpStatusCode.NotFound))
    }

    private suspend fun errorFor(status: HttpStatusCode): FetchError {
        val client = HttpClient(MockEngine { respondError(status) }) { expectSuccess = true }
        val fetcher = fetcher(fetchPage = {
            client.get("http://test/")
            error("unreachable")
        })
        return (fetcher.pullNextPage() as FetchResult.Error).error
    }

    @Test
    fun `persist failure reports StorageError and keeps cursor`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = "2", completedAt = null)
        val fetcher = fetcher(
            fetchPage = { FetchPage(listOf("a"), nextCursor = "3") },
            persistPage = { throw IllegalStateException("disk full") },
        )

        val result = fetcher.pullNextPage() as FetchResult.Error

        assertEquals(FetchError.StorageError, result.error)
        assertEquals("2", store.map[PhloemModel.PlantCatalog]?.cursor)
    }

    // ---- freshness ----
    @Test
    fun `isFresh false when never completed`() = runTest {
        assertFalse(fetcher().isFresh())

        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = "4", completedAt = null)
        assertFalse(fetcher().isFresh())
    }

    @Test
    fun `isFresh true within ttl and false after`() = runTest {
        store.map[PhloemModel.PlantCatalog] = FetchMetadata(cursor = null, completedAt = currentTime)

        currentTime += 23.hours.inWholeMilliseconds
        assertTrue(fetcher().isFresh())

        currentTime += 2.hours.inWholeMilliseconds
        assertFalse(fetcher().isFresh())
    }

    // ---- concurrency ----
    @Test
    fun `concurrent pulls serialize on the mutex`() = runTest {
        val gate = CompletableDeferred<Unit>()
        var firstCall = true
        val fetcher = fetcher(fetchPage = { cursor ->
            requestedCursors += cursor
            if (firstCall) {
                firstCall = false
                gate.await()   // hold the mutex mid-pull
            }
            FetchPage(listOf("x"), nextCursor = ((cursor?.toInt() ?: 1) + 1).toString())
        })

        val first = launch { fetcher.pullNextPage() }
        val second = launch { fetcher.pullNextPage() }
        testScheduler.advanceUntilIdle()

        assertEquals(listOf<String?>(null), requestedCursors)   // second still parked
        gate.complete(Unit)
        first.join()
        second.join()

        // second read the cursor committed by first -> serialized, no duplicate page
        assertEquals(listOf(null, "2"), requestedCursors)
    }
}
