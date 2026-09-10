package com.ilynehdev.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.ilynehdev.core.database.entities.SavedPlantEntity
import com.ilynehdev.core.database.projections.SavedPlantsRow
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlantsDao {
    @Upsert
    suspend fun upsertSavedPlant(plant: SavedPlantEntity)

    @Upsert
    suspend fun upsertSavedPlants(plants: List<SavedPlantEntity>)

    @Query("DELETE FROM saved_plants WHERE plant_id = :plantId")
    suspend fun deleteSavedPlant(plantId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_plants WHERE plant_id = :plantId) ")
    fun observeIsSaved(plantId: Long): Flow<Boolean>

    @Query("""
        SELECT id, common_name, scientific_name, watering, sunlight, image_thumbnail, saved_plants.saved_at
        FROM plants
        INNER JOIN saved_plants ON plants.id == saved_plants.plant_id
        ORDER BY saved_at DESC
    """)
    fun observeSavedPlants(): Flow<List<SavedPlantsRow>>
}
