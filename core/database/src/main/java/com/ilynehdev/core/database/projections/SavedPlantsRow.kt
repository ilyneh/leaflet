package com.ilynehdev.core.database.projections

import androidx.room3.ColumnInfo

data class SavedPlantsRow(
    val id: Long,
    @ColumnInfo(name = "common_name") val commonName: String?,
    @ColumnInfo(name = "scientific_name") val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    @ColumnInfo(name = "image_thumbnail") val thumbnail: String?,
    @ColumnInfo(name = "saved_at") val savedAt: Long,
)
