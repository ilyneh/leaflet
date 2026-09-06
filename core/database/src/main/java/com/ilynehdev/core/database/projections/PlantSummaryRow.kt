package com.ilynehdev.core.database.projections

import androidx.room3.ColumnInfo

data class PlantSummaryRow(
    val id: Long,
    @ColumnInfo("common_name") val commonName: String?,
    @ColumnInfo("scientific_name") val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    @ColumnInfo(name = "image_thumbnail") val thumbnail: String?,
)