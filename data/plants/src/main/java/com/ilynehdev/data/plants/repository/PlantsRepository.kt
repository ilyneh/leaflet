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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.hours

interface PlantsRepository {

    fun observePlant(plantId: String): Flow<Plant>

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

    override fun observePlant(plantId: String): Flow<Plant> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observePlants(): Flow<PagingData<Plant>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PlantsRemoteMediator(fetcher),
            pagingSourceFactory = { dao.pagedSummaries() }
        ).flow
            .map { pagingData -> pagingData.map { it.toPlant() } }
    }

    override fun searchPlant(name: String): Flow<PagingData<Plant>> {
        TODO("Not yet implemented")
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}