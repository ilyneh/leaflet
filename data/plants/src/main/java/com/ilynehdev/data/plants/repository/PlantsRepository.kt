package com.ilynehdev.data.plants.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.phloem.FetchMetadataStore
import com.ilynehdev.core.phloem.FetchPage
import com.ilynehdev.core.phloem.PhloemFetcherImpl
import com.ilynehdev.core.phloem.PhloemModel
import com.ilynehdev.core.phloem.Transactor
import com.ilynehdev.data.plants.mapper.toEntity
import com.ilynehdev.data.plants.mapper.toPlant
import com.ilynehdev.data.plants.model.Plant
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

interface PlantsRepository {

    fun observePlant(plantId: Long): Flow<Plant?>

    fun observePlants(): Flow<PagingData<Plant>>

    fun searchPlant(name: String): Flow<PagingData<Plant>>
}

class PlantsRepositoryImpl(
    private val dao: PlantsDao,
    private val api: PlantsApi,
    transactor: Transactor,
    metadataStore: FetchMetadataStore,
) : PlantsRepository {

    private val fetcher = PhloemFetcherImpl(
        model = PhloemModel.PlantCatalog,
        ttl = 24.hours,
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
            val entities = items.map { it.toEntity() }
            dao.upsertPlants(entities)
        }
    )

    override fun observePlant(plantId: Long): Flow<Plant?> =
        dao.observePlant(plantId).map { it?.toPlant() }

    @OptIn(ExperimentalPagingApi::class)
    override fun observePlants(): Flow<PagingData<Plant>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PlantsRemoteMediator(fetcher),
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
                dao.upsertPlants(page.items.map { it.toEntity() })
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