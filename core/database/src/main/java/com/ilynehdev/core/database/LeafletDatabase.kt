package com.ilynehdev.core.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.ilynehdev.core.database.converters.PlantConverters
import com.ilynehdev.core.database.dao.FetchMetadataDao
import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.database.dao.PlantDiseasesDao
import com.ilynehdev.core.database.dao.SavedPlantsDao
import com.ilynehdev.core.database.entities.FetchMetadataEntity
import com.ilynehdev.core.database.entities.PlantDiseaseEntity
import com.ilynehdev.core.database.entities.PlantEntity
import com.ilynehdev.core.database.entities.SavedPlantEntity

@Database(
    entities = [
        PlantEntity::class,
        PlantDiseaseEntity::class,
        FetchMetadataEntity::class,
        SavedPlantEntity::class],
    version = 4
)
@ColumnTypeConverters(PlantConverters::class)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class LeafletDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantsDao
    abstract fun plantDiseaseDao(): PlantDiseasesDao
    abstract fun fetchMetadataDao(): FetchMetadataDao
    abstract fun savedPlantDao(): SavedPlantsDao
}