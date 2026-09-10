package com.ilynehdev.data.plants.repository

import android.content.Context
import androidx.paging.testing.ErrorRecovery
import androidx.paging.testing.asSnapshot
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.database.LeafletDatabase
import com.ilynehdev.core.network.plants.api.Page
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemItemFetcherImpl
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadata
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.core.phloem.pagefetcher.PhloemModel
import com.ilynehdev.data.common.RefreshResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException
import kotlin.time.Duration.Companion.days

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PlantsRepositoryTest {

    private class FakePlantsApi : PlantsApi {
        val pages = mutableMapOf<Int, Page<PlantDto>>()
        val searchResults = mutableMapOf<String, Page<PlantDto>>()
        var failWith: Exception? = null
        var failOnPage: Int? = null
        val requestedQueries = mutableListOf<String?>()
        val requestedPages = mutableListOf<Int>()

        override suspend fun getPlants(page: Int, query: String?): Page<PlantDto> {
            failWith?.let { throw it }
            if (page == failOnPage) throw IOException("interrupted at page $page")
            requestedQueries += query
            requestedPages += page
            return if (query == null) {
                pages[page] ?: Page(emptyList(), nextKey = null)
            } else {
                searchResults[query] ?: Page(emptyList(), nextKey = null)
            }
        }

        val details = mutableMapOf<Long, PlantDto>()
        val requestedDetailIds = mutableListOf<Long>()

        override suspend fun getPlant(id: Long): PlantDto {
            failWith?.let { throw it }
            requestedDetailIds += id
            return details[id] ?: error("no detail stubbed for id $id")
        }
    }

    private class FakeMetadataStore : FetchMetadataStore {
        val map = mutableMapOf<PhloemModel, FetchMetadata>()
        override suspend fun get(model: PhloemModel) = map[model]
        override suspend fun save(model: PhloemModel, metadata: FetchMetadata) {
            map[model] = metadata
        }
    }

    private lateinit var db: LeafletDatabase
    private val api = FakePlantsApi()
    private val store = FakeMetadataStore()
    private val DETAILS_TTL = 7.days
    private var nowMillis = 1_000_000L
    private lateinit var repo: PagedPlantsRepository
    private lateinit var detailRepo: PlantsRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LeafletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = PagedPlantsRepositoryImpl(
            dao = db.plantDao(),
            api = api,
            transactor = { it() },
            metadataStore = store,
            now = { nowMillis },
        )
        detailRepo = PlantsRepositoryImpl(
            dao = db.plantDao(),
            savedDao = db.savedPlantDao(),
            api = api,
            fetcher = PhloemItemFetcherImpl(),
            freshness = Freshness(ttl = DETAILS_TTL, now = { nowMillis }),
            now = { nowMillis },
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun dto(id: Long, commonName: String) = PlantDto(id = id, commonName = commonName)

    // ---- observePlant ----
    @Test
    fun `observePlant maps entity to domain model`() = runTest {
        api.pages[1] = Page(listOf(dto(7, "Monstera Deliciosa")), nextKey = null)
        repo.observePlants().asSnapshot()   // fills db through the mediator

        val plant = detailRepo.observePlant(7).first()

        assertEquals("Monstera Deliciosa", plant?.commonName)
        assertEquals(7L, plant?.id)
    }

    @Test
    fun `observePlant emits null for missing row`() = runTest {
        assertNull(detailRepo.observePlant(99).first())
    }

    // ---- observePlants (mediator path) ----
    @Test
    fun `observePlants pulls catalog pages through mediator into db`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe"), dto(2, "Basil")), nextKey = 2)
        api.pages[2] = Page(listOf(dto(3, "Cactus")), nextKey = null)

        val items = repo.observePlants().asSnapshot()

        assertEquals(listOf("Aloe", "Basil", "Cactus"), items.map { it.commonName })
        // crawl completed: cursor cleared, ttl clock started
        assertNull(store.map[PhloemModel.PlantCatalog]?.cursor)
        assertTrue((store.map[PhloemModel.PlantCatalog]?.completedAt ?: 0) > 0)
    }

    @Test
    fun `observePlants serves cached rows without network when fresh`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = null)
        repo.observePlants().asSnapshot()   // completes crawl, stamps completedAt

        api.failWith = IOException("network gone")

        val items = repo.observePlants().asSnapshot()

        assertEquals(listOf("Aloe"), items.map { it.commonName })
    }

    @Test
    fun `crawl interrupted after first page resumes from cursor without refetching`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = 2)
        api.failOnPage = 2

        // First session: page 1 lands, page 2 fails -> partial catalog, no crash.
        repo.observePlants().asSnapshot(
            onError = { ErrorRecovery.RETURN_CURRENT_SNAPSHOT }
        )

        // page 1 committed to the db despite the interruption
        assertEquals(
            listOf("Aloe"),
            db.plantDao().observeSummaries().first().map { it.commonName },
        )
        val meta = store.map[PhloemModel.PlantCatalog]
        assertEquals("2", meta?.cursor)      // saved with page 1, points at first unfetched page
        assertNull(meta?.completedAt)        // half-crawled is not synced

        // Second session: network back.
        api.failOnPage = null
        api.pages[2] = Page(listOf(dto(2, "Basil")), nextKey = null)

        val full = repo.observePlants().asSnapshot()

        assertEquals(listOf("Aloe", "Basil"), full.map { it.commonName })
        // page 1 was fetched exactly once across both sessions
        assertEquals(listOf(1, 2), api.requestedPages)
    }

    // ---- searchPlant ----
    @Test
    fun `searchPlant serves local matches when remote fails`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Monstera"), dto(2, "Basil")), nextKey = null)
        repo.observePlants().asSnapshot()   // seed local catalog
        api.failWith = IOException("offline")

        val items = repo.searchPlant("monstera").asSnapshot()

        assertEquals(listOf("Monstera"), items.map { it.commonName })
    }

    @Test
    fun `searchPlant merges remote hits into local results`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Monstera Local")), nextKey = null)
        repo.observePlants().asSnapshot()
        api.searchResults["monstera"] = Page(listOf(dto(50, "Monstera Remote")), nextKey = 2)

        val items = repo.searchPlant("monstera").asSnapshot()

        assertEquals(
            listOf("Monstera Local", "Monstera Remote"),
            items.map { it.commonName.orEmpty() }.sorted(),
        )
        // remote hit permanently enriched the catalog table
        assertEquals("Monstera Remote", detailRepo.observePlant(50).first()?.commonName)
    }

    @Test
    fun `searchPlant passes query to api`() = runTest {
        repo.searchPlant("fern").asSnapshot()

        assertTrue("fern" in api.requestedQueries)
    }

    // ---- refreshPlantDetails (cache-through) ----
    @Test
    fun `refreshPlantDetails fetches, stores and stamps the row`() = runTest {
        api.details[7] = PlantDto(id = 7, commonName = "Monstera", description = "Big leaves")

        val result = detailRepo.refreshPlantDetails(7)

        assertEquals(RefreshResult.Refreshed, result)
        val row = db.plantDao().getById(7)
        assertEquals("Big leaves", row?.description)
        assertEquals(nowMillis, row?.detailsSyncedAt)
    }

    @Test
    fun `refreshPlantDetails skips network while row is fresh`() = runTest {
        api.details[7] = PlantDto(id = 7, commonName = "Monstera")
        detailRepo.refreshPlantDetails(7)

        nowMillis += 1_000  // well inside the TTL
        val result = detailRepo.refreshPlantDetails(7)

        assertEquals(RefreshResult.AlreadyFresh, result)
        assertEquals(listOf(7L), api.requestedDetailIds)  // fetched exactly once
    }

    @Test
    fun `forced refresh bypasses ttl and refetches a fresh row`() = runTest {
        api.details[7] = PlantDto(id = 7, commonName = "Monstera")
        detailRepo.refreshPlantDetails(7)

        nowMillis += 1_000  // still fresh
        val result = detailRepo.refreshPlantDetails(7, force = true)

        assertEquals(RefreshResult.Refreshed, result)
        assertEquals(listOf(7L, 7L), api.requestedDetailIds)
        assertEquals(nowMillis, db.plantDao().getById(7)?.detailsSyncedAt)
    }

    @Test
    fun `refreshPlantDetails refetches after ttl expires`() = runTest {
        api.details[7] = PlantDto(id = 7, commonName = "Monstera")
        detailRepo.refreshPlantDetails(7)

        nowMillis += DETAILS_TTL.inWholeMilliseconds + 1
        detailRepo.refreshPlantDetails(7)

        assertEquals(listOf(7L, 7L), api.requestedDetailIds)
        assertEquals(nowMillis, db.plantDao().getById(7)?.detailsSyncedAt)
    }

    @Test
    fun `refreshPlantDetails classifies failure and keeps cached row`() = runTest {
        api.details[7] = PlantDto(id = 7, commonName = "Monstera", description = "Big leaves")
        detailRepo.refreshPlantDetails(7)

        nowMillis += DETAILS_TTL.inWholeMilliseconds + 1
        api.failWith = IOException("offline")

        val result = detailRepo.refreshPlantDetails(7)

        assertEquals(RefreshResult.Failed(FetchError.Offline), result)
        assertEquals("Big leaves", db.plantDao().getById(7)?.description)
    }

    // ---- saved plants ----
    @Test
    fun `updateSavedPlant round-trips through observeIsSaved`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = null)
        repo.observePlants().asSnapshot()   // seed catalog row for the FK

        assertFalse(detailRepo.observeIsSaved(1).first())

        detailRepo.updateSavedPlant(1, saved = true)
        assertTrue(detailRepo.observeIsSaved(1).first())

        detailRepo.updateSavedPlant(1, saved = false)
        assertFalse(detailRepo.observeIsSaved(1).first())
    }

    @Test
    fun `updateSavedPlant is idempotent and refreshes savedAt`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = null)
        repo.observePlants().asSnapshot()

        detailRepo.updateSavedPlant(1, saved = true)
        nowMillis += 5_000
        detailRepo.updateSavedPlant(1, saved = true)

        val saved = detailRepo.observeSavedPlants().first()
        assertEquals(1, saved.size)
        assertEquals(nowMillis, saved.single().savedAt)
    }

    @Test
    fun `unsaving a plant that is not saved is a no-op`() = runTest {
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = null)
        repo.observePlants().asSnapshot()

        detailRepo.updateSavedPlant(1, saved = false)

        assertTrue(detailRepo.observeSavedPlants().first().isEmpty())
    }

    @Test
    fun `observeSavedPlants maps plant fields and orders by most recently saved`() = runTest {
        api.pages[1] = Page(
            listOf(dto(1, "Aloe"), dto(2, "Basil"), dto(3, "Cactus")),
            nextKey = null,
        )
        repo.observePlants().asSnapshot()

        detailRepo.updateSavedPlant(1, saved = true)
        nowMillis += 1_000
        detailRepo.updateSavedPlant(3, saved = true)

        val saved = detailRepo.observeSavedPlants().first()

        assertEquals(listOf(3L, 1L), saved.map { it.id })  // newest first
        val aloe = saved.single { it.id == 1L }
        assertEquals("Aloe", aloe.commonName)
        assertEquals(nowMillis - 1_000, aloe.savedAt)
    }

    @Test
    fun `list crawl does not clobber a detail-synced row`() = runTest {
        api.details[1] = PlantDto(id = 1, commonName = "Aloe", description = "Succulent")
        detailRepo.refreshPlantDetails(1)

        // Catalog crawl returns the same plant as a sparse list row.
        api.pages[1] = Page(listOf(dto(1, "Aloe")), nextKey = null)
        repo.observePlants().asSnapshot()

        val row = db.plantDao().getById(1)
        assertEquals("Succulent", row?.description)      // detail survived
        assertEquals(nowMillis, row?.detailsSyncedAt)    // stamp survived
    }
}
