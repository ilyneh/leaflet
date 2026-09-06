package com.ilynehdev.core.database.dao

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

    @Query("SELECT id, common_name, image_thumbnail FROM plants ORDER BY common_name")
    fun observeSummaries(): Flow<List<PlantSummaryRow>>
}
