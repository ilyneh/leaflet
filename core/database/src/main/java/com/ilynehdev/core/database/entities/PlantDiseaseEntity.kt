package com.ilynehdev.core.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class PlantDiseaseSectionColumn(
    val subtitle: String? = null,
    val description: String? = null,
)

@Entity
data class PlantDiseaseEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo("common_name") val commonName: String? = null,
    @ColumnInfo("scientific_name") val scientificName: String? = null,   // single string here, not list
    @ColumnInfo("other_name") val otherName: List<String>? = null,
    val family: String? = null,
    val description: List<PlantDiseaseSectionColumn>? = null,
    val solution: List<PlantDiseaseSectionColumn>? = null,
    val host: List<String>? = null,
    val images: List<ImageColumn>? = null,
)
