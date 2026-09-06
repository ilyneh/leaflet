package com.ilynehdev.core.database.projections

import androidx.room3.ColumnInfo
import com.ilynehdev.core.database.entities.ImageColumn

data class PlantDiseaseSummaryRow(
    val id: Long,
    @ColumnInfo("common_name") val commonName: String?,
    @ColumnInfo("scientific_name") val scientificName: String?,
    val host: List<String>?,
    val images: List<ImageColumn>?,
) {
    // Derived here instead of json_extract in SQL: Android bundles the JSON1
    // extension only from API 30, and minSdk is 23.
    val thumbnail: String? get() = images?.firstOrNull()?.thumbnail
}
