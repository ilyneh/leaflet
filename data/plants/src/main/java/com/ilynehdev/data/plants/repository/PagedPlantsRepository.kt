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
import com.ilynehdev.data.plants.mapper.toEntity
import com.ilynehdev.data.plants.mapper.toPlant
import com.ilynehdev.data.plants.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.hours

interface PagedPlantsRepository {
    fun observePlants(): Flow<PagingData<Plant>>

    fun searchPlant(name: String): Flow<PagingData<Plant>>
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

    // Local-first: Room is the merge point. The paged flow serves local matches
    // immediately; in parallel one remote search page is upserted into the same
    // table, which invalidates the PagingSource and re-emits with remote hits.
    // Remote failure (offline) is swallowed — local results stand alone.
    override fun searchPlant(name: String): Flow<PagingData<Plant>> = channelFlow {
        launch {
            try {
                val page = api.getPlants(page = 1, query = name)
                upsertListRows(page.items)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }

        Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { dao.searchSummaries(name) }
        ).flow
            .map { pagingData -> pagingData.map { it.toPlant() } }
            .collect { send(it) }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}
