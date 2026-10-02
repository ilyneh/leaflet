package com.ilynehdev.feature.plants.data.repository

import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.database.dao.SavedPlantsDao
import com.ilynehdev.core.database.entities.SavedPlantEntity
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemItemFetcher
import com.ilynehdev.core.time.TimeProvider
import com.ilynehdev.core.data.RefreshError
import com.ilynehdev.core.data.RefreshResult
import com.ilynehdev.feature.plants.data.model.mapper.toEntity
import com.ilynehdev.feature.plants.data.model.mapper.toPlantDetails
import com.ilynehdev.feature.plants.data.model.mapper.toSavedPlants
import com.ilynehdev.feature.plants.data.model.PlantDetails
import com.ilynehdev.feature.plants.data.model.PlantId
import com.ilynehdev.feature.plants.data.model.SavedPlant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlantsRepository {

    fun observePlant(plantId: PlantId): Flow<PlantDetails?>

    fun observeIsSaved(plantId: PlantId): Flow<Boolean>
    fun observeSavedPlants(): Flow<List<SavedPlant>>

    suspend fun updateSavedPlant(plantId: PlantId, saved: Boolean)
    /**
     * Cache-through fetch of the full plant record; [observePlant] emits the
     * update. [force] skips the TTL check (pull-to-refresh); otherwise the
     * network is hit only when the row is stale or missing.
     */
    suspend fun refreshPlantDetails(plantId: PlantId, force: Boolean = false): RefreshResult
}

internal class PlantsRepositoryImpl(
    private val dao: PlantsDao,
    private val savedDao: SavedPlantsDao,
    private val api: PlantsApi,
    private val fetcher: PhloemItemFetcher,
    private val freshness: Freshness,
    private val timeProvider: TimeProvider,
) : PlantsRepository {

    override fun observePlant(plantId: PlantId): Flow<PlantDetails?> =
        dao.observePlant(plantId.value).map { it?.toPlantDetails() }

    override fun observeIsSaved(plantId: PlantId): Flow<Boolean> =
        savedDao.observeIsSaved(plantId.value)

    override fun observeSavedPlants(): Flow<List<SavedPlant>> =
        savedDao.observeSavedPlants().map { it.toSavedPlants() }

    override suspend fun updateSavedPlant(plantId: PlantId, saved: Boolean) {
        if (saved) {
            savedDao.upsertSavedPlant(SavedPlantEntity(plantId.value, savedAt = timeProvider.currentTimeMillis()))
        } else {
            savedDao.deleteSavedPlant(plantId.value)
        }
    }

    override suspend fun refreshPlantDetails(plantId: PlantId, force: Boolean): RefreshResult {
        if (!force && freshness.isFresh(dao.getById(plantId.value)?.detailsSyncedAt)) {
            return RefreshResult.AlreadyFresh
        }

        val error = fetcher.fetchItem(
            fetch = {
                api.getPlant(plantId.value)
            },
            persist = { dto ->
                dao.upsertPlant(dto.toEntity().copy(detailsSyncedAt = freshness.newTimestamp()))
            }
        )

        return when (error) {
            null -> RefreshResult.Refreshed
            else -> RefreshResult.Failed(error.toRefreshError())
        }
    }
}

private fun FetchError.toRefreshError(): RefreshError = when (this) {
    FetchError.Offline -> RefreshError.Offline
    FetchError.RateLimited -> RefreshError.RateLimited
    FetchError.ServerDown -> RefreshError.ServerDown
    FetchError.Forbidden -> RefreshError.Unauthorized
    FetchError.StorageError -> RefreshError.StorageError
    FetchError.General -> RefreshError.Unknown
}
