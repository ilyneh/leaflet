package com.ilynehdev.core.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "saved_plants",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("plant_id"),
        )],
)
data class SavedPlantEntity(
    @PrimaryKey
    @ColumnInfo("plant_id") val plantId: Long,
    @ColumnInfo("saved_at") val savedAt: Long
)
