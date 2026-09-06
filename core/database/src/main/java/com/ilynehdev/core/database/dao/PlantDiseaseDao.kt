package com.ilynehdev.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.ilynehdev.core.database.entities.PlantDiseaseEntity
import com.ilynehdev.core.database.projections.PlantDiseaseSummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDiseaseDao {

    @Upsert
    fun upsertPlantDiseases(plants: List<PlantDiseaseEntity>)

    @Upsert
    fun upsertPlantDisease(plant: PlantDiseaseEntity)

    @Query(
        """
        SELECT id, common_name, scientific_name, host,
               json_extract(images, '$[0].thumbnail') AS image_thumbnail
        FROM plant_diseases ORDER BY common_name
        """
    )
    fun observeSummaries(): Flow<List<PlantDiseaseSummaryRow>>


}