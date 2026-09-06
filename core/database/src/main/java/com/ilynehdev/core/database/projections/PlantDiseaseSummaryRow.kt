package com.ilynehdev.core.database.projections

import androidx.room3.ColumnInfo

data class PlantDiseaseSummaryRow(
    val id: Long,
    @ColumnInfo("common_name") val commonName: String?,
    @ColumnInfo("scientific_name") val scientificName: String?,
    val host: List<String>?,
    @ColumnInfo("image_thumbnail") val thumbnail: String?,
)
