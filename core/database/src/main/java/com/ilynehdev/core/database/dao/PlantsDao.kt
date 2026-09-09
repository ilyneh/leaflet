package com.ilynehdev.core.database.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.ilynehdev.core.database.entities.PlantEntity
import com.ilynehdev.core.database.projections.PlantSummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantsDao {

    @Upsert
    suspend fun upsertPlants(plants: List<PlantEntity>)

    @Upsert
    suspend fun upsertPlant(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: Long): PlantEntity?

    @Query("SELECT id FROM plants WHERE id IN (:ids) AND details_synced_at IS NOT NULL")
    suspend fun detailSyncedIds(ids: List<Long>): List<Long>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun observePlant(id: Long): Flow<PlantEntity?>

    @Query("SELECT * FROM plants ORDER BY common_name")
    fun observePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants ORDER BY common_name")
    fun pagedPlants(): PagingSource<Int, PlantEntity>

    @Query(
        """
        SELECT id, common_name, scientific_name, watering, sunlight, image_thumbnail
        FROM plants ORDER BY common_name
        """
    )
    fun observeSummaries(): Flow<List<PlantSummaryRow>>

    @Query(
        """
        SELECT id, common_name, scientific_name, watering, sunlight, image_thumbnail
        FROM plants ORDER BY id
        """
    )
    fun pagedSummaries(): PagingSource<Int, PlantSummaryRow>

    // scientific_name is a JSON-encoded list; LIKE still substring-matches inside it.
    @Query(
        """
        SELECT id, common_name, scientific_name, watering, sunlight, image_thumbnail
        FROM plants
        WHERE common_name LIKE '%' || :query || '%'
           OR scientific_name LIKE '%' || :query || '%'
        ORDER BY common_name
        """
    )
    fun searchSummaries(query: String): PagingSource<Int, PlantSummaryRow>
}
