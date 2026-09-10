package com.ilynehdev.data.plants.repository

import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemFetcher
import com.ilynehdev.data.common.RefreshResult
import com.ilynehdev.data.plants.mapper.toEntity
import com.ilynehdev.data.plants.mapper.toPlantDetails
import com.ilynehdev.data.plants.model.PlantDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlantsRepository {

    fun observePlant(plantId: Long): Flow<PlantDetails?>
    /**
     * Cache-through fetch of the full plant record; [observePlant] emits the
     * update. [force] skips the TTL check (pull-to-refresh); otherwise the
     * network is hit only when the row is stale or missing.
     */
    suspend fun refreshPlantDetails(plantId: Long, force: Boolean = false): RefreshResult
}

class PlantsRepositoryImpl(
    private val dao: PlantsDao,
    private val api: PlantsApi,
    private val fetcher: PhloemFetcher,
    private val freshness: Freshness,
) : PlantsRepository {

    override fun observePlant(plantId: Long): Flow<PlantDetails?> =
        dao.observePlant(plantId).map { it?.toPlantDetails() }

    override suspend fun refreshPlantDetails(plantId: Long, force: Boolean): RefreshResult {
        if (!force && freshness.isFresh(dao.getById(plantId)?.detailsSyncedAt)) {
            return RefreshResult.AlreadyFresh
        }

        val error = fetcher.fetchItem(
            fetch = {
                api.getPlant(plantId)
            },
            persist = { dto ->
                dao.upsertPlant(dto.toEntity().copy(detailsSyncedAt = freshness.newTimestamp()))
            }
        )

        return when (error) {
            null -> RefreshResult.Refreshed
            else -> RefreshResult.Failed(error)
        }
    }
}
