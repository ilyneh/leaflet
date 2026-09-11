package com.ilynehdev.data.plants.repository

import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.database.dao.SavedPlantsDao
import com.ilynehdev.core.database.entities.SavedPlantEntity
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemItemFetcher
import com.ilynehdev.data.common.RefreshResult
import com.ilynehdev.data.plants.mapper.toEntity
import com.ilynehdev.data.plants.mapper.toPlantDetails
import com.ilynehdev.data.plants.mapper.toSavedPlants
import com.ilynehdev.data.plants.model.PlantDetails
import com.ilynehdev.data.plants.model.SavedPlant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlantsRepository {

    fun observePlant(plantId: Long): Flow<PlantDetails?>

    fun observeIsSaved(plantId: Long): Flow<Boolean>
    fun observeSavedPlants(): Flow<List<SavedPlant>>

    suspend fun updateSavedPlant(plantId: Long, saved: Boolean)
    /**
     * Cache-through fetch of the full plant record; [observePlant] emits the
     * update. [force] skips the TTL check (pull-to-refresh); otherwise the
     * network is hit only when the row is stale or missing.
     */
    suspend fun refreshPlantDetails(plantId: Long, force: Boolean = false): RefreshResult
}

class PlantsRepositoryImpl(
    private val dao: PlantsDao,
    private val savedDao: SavedPlantsDao,
    private val api: PlantsApi,
    private val fetcher: PhloemItemFetcher,
    private val freshness: Freshness,
    private val now: () -> Long,
) : PlantsRepository {

    override fun observePlant(plantId: Long): Flow<PlantDetails?> =
        dao.observePlant(plantId).map { it?.toPlantDetails() }

    override fun observeIsSaved(plantId: Long): Flow<Boolean> =
        savedDao.observeIsSaved(plantId)

    override fun observeSavedPlants(): Flow<List<SavedPlant>> =
        savedDao.observeSavedPlants().map { it.toSavedPlants() }

    override suspend fun updateSavedPlant(plantId: Long, saved: Boolean) {
        if (saved) {
            savedDao.upsertSavedPlant(SavedPlantEntity(plantId, savedAt = now()))
        } else {
            savedDao.deleteSavedPlant(plantId)
        }
    }

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
