package com.ilynehdev.core.database.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.ilynehdev.core.database.entities.PlantEntity
import com.ilynehdev.core.database.projections.PlantSummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Upsert
    fun upsertPlants(plants: List<PlantEntity>)

    @Upsert
    fun upsertPlant(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: Long): PlantEntity?

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
        FROM plants ORDER BY common_name
        """
    )
    fun pagedSummaries(): PagingSource<Int, PlantSummaryRow>
}
