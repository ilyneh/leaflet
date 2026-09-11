package com.ilynehdev.data.plants.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.core.phloem.pagefetcher.FetchPage
import com.ilynehdev.core.phloem.pagefetcher.PhloemModel
import com.ilynehdev.core.phloem.pagefetcher.PhloemPageFetcherImpl
import com.ilynehdev.core.phloem.Transactor
import com.ilynehdev.data.plants.filters.FilterQueryParams
import com.ilynehdev.data.plants.model.mapper.toEntity
import com.ilynehdev.data.plants.model.mapper.toPlant
import com.ilynehdev.data.plants.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.hours

interface PagedPlantsRepository {
    fun observePlants(): Flow<PagingData<Plant>>

    fun observeFilteredPlants(
        query: String,
        savedOnly: Boolean,
        searchParams: FilterQueryParams,
    ): Flow<List<Plant>>
}

class PagedPlantsRepositoryImpl(
    private val dao: PlantsDao,
    private val api: PlantsApi,
    transactor: Transactor,
    metadataStore: FetchMetadataStore,
    private val now: () -> Long = System::currentTimeMillis,
) : PagedPlantsRepository {

    private val pageFetcher = PhloemPageFetcherImpl(
        model = PhloemModel.PlantCatalog,
        freshness = Freshness(
            ttl = 24.hours,
            now = now
        ),
        transactor = transactor,
        fetchMetadataStore = metadataStore,
        fetchPage = { cursor ->
            val page = api.getPlants(page = cursor?.toIntOrNull() ?: 1)
            FetchPage(
                items = page.items,
                nextCursor = page.nextKey?.toString()
            )
        },
        persistPage = { items ->
            upsertListRows(items)
        }
    )

    // One successful remote fetch per (query, params) per process, LRU-capped:
    // filter toggles and resubscribes re-create the flow, and the rows they
    // need are already upserted. Failures are not recorded, so going back
    // online retries. Access-ordered map keeps recently repeated searches
    // deduped while old ones age out and become refetchable.
    private val completedSearches = object : LinkedHashMap<Pair<String, FilterQueryParams>, Unit>(
        16,
        0.75f,
        true
    ) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<Pair<String, FilterQueryParams>, Unit>,
        ) = size > MAX_COMPLETED_SEARCHES
    }

    // List responses are sparse; upserting one over a detail-synced row would
    // null out the fetched details, so those rows are skipped.
    private suspend fun upsertListRows(items: List<PlantDto>) {
        val entities = items.map { it.toEntity() }
        val protected = dao.detailSyncedIds(entities.map { it.id }).toSet()
        dao.upsertPlants(entities.filterNot { it.id in protected })
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observePlants(): Flow<PagingData<Plant>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PagedPlantsRemoteMediator(pageFetcher),
            pagingSourceFactory = { dao.pagedSummaries() }
        ).flow
            .map { pagingData -> pagingData.map { it.toPlant() } }
    }

    // Local-first: Room is the merge point. The list flow serves local matches
    // immediately; in parallel one remote search page is upserted into the same
    // table, which re-emits the flow with remote hits. Remote failure (offline)
    // is swallowed — local results stand alone. SQL narrows by query and saved
    // membership; the in-memory filter predicates live in
    // ObserveFilteredPlantsUseCase. Server params only pre-narrow the fetch.
    override fun observeFilteredPlants(
        query: String,
        savedOnly: Boolean,
        searchParams: FilterQueryParams,
    ): Flow<List<Plant>> = channelFlow {
        if (query.isNotBlank()) {
            launch { fetchSearchPage(query, searchParams) }
        }

        dao.observePlantsFiltered(escapeLike(query.trim()), savedOnly)
            .map { entities -> entities.map { it.toPlant() } }
            .collect { send(it) }
    }

    private suspend fun fetchSearchPage(
        query: String,
        params: FilterQueryParams = FilterQueryParams(),
    ) {
        val key = query to params
        synchronized(completedSearches) {
            if (completedSearches.containsKey(key)) return
        }

        try {
            val page = api.getPlants(
                page = 1,
                query = query,
                sunlight = params.sunlight,
                watering = params.watering,
                poisonous = params.poisonous,
            )
            upsertListRows(page.items)
            synchronized(completedSearches) { completedSearches[key] = Unit }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    private fun escapeLike(query: String): String = query
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

    companion object {
        const val NETWORK_PAGE_SIZE = 30
        private const val MAX_COMPLETED_SEARCHES = 50
    }
}
