package com.ilynehdev.core.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.ilynehdev.core.database.converters.PlantConverters
import com.ilynehdev.core.database.dao.PlantDao
import com.ilynehdev.core.database.dao.PlantDiseaseDao
import com.ilynehdev.core.database.entities.PlantDiseaseEntity
import com.ilynehdev.core.database.entities.PlantEntity

@Database(
    entities = [
        PlantEntity::class,
        PlantDiseaseEntity::class],
    version = 1
)
@ColumnTypeConverters(PlantConverters::class)
abstract class LeafletDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun plantDiseaseDao(): PlantDiseaseDao
}