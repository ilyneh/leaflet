package com.ilynehdev.core.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo("common_name") val commonName: String?,
    @ColumnInfo("scientific_name") val scientificName: List<String>?,
    @ColumnInfo("other_name") val otherName: List<String>? = null,
    val family: String?,
    val genus: String? = null,
    @ColumnInfo("species_epithet") val speciesEpithet: String? = null,
    val cultivar: String? = null,
    val variety: String? = null,
    val origin: List<String>? = null,
    val type: String?,
    val dimensions: List<DimensionsColumn>? = null,
    val cycle: String?,
    val watering: String?,
    @ColumnInfo("plant_anatomy") val plantAnatomy: List<AnatomyColumn>? = null,
    val sunlight: List<String>? = null,
    @ColumnInfo("pruning_month") val pruningMonth: List<String>? = null,
    @ColumnInfo("pruning_count") val pruningCount: List<PruningCountColumn>? = null,
    val seeds: Boolean? = null,
    val attracts: List<String>? = null,
    val propagation: List<String>? = null,
    val flowers: Boolean? = null,
    @ColumnInfo("flowering_season") val floweringSeason: String? = null,
    val soil: List<String>? = null,
    @ColumnInfo("pest_susceptibility") val pestSusceptibility: List<String>? = null,
    @ColumnInfo("growth_rate") val growthRate: String? = null,
    val maintenance: String? = null,
    val medicinal: Boolean? = null,
    @ColumnInfo("poisonous_to_humans") val poisonousToHumans: Boolean? = null,
    @ColumnInfo("poisonous_to_pets") val poisonousToPets: Boolean? = null,
    @ColumnInfo("drought_tolerant") val droughtTolerant: Boolean? = null,
    @ColumnInfo("salt_tolerant") val saltTolerant: Boolean? = null,
    val thorny: Boolean? = null,
    val invasive: Boolean? = null,
    val rare: Boolean? = null,
    val tropical: Boolean? = null,
    val cuisine: Boolean? = null,
    val indoor: Boolean? = null,
    @SerialName("care_level") val careLevel: String? = null,
    val description: String? = null,

    @Embedded("hardiness_") val hardiness: HardinessColumn? = null,
    @Embedded("watering_benchmark_") val wateringBenchmark: WateringBenchmarkColumn?,
    @Embedded("image_") val defaultImage: ImageColumn? = null,
)

@Serializable
data class ImageColumn(
    @SerialName("image_id") val imageId: Long? = null,
    val license: Int? = null,
    @SerialName("license_name") val licenseName: String? = null,
    @SerialName("license_url") val licenseUrl: String? = null,
    @SerialName("original_url") val originalUrl: String? = null,
    @SerialName("regular_url") val regularUrl: String? = null,
    @SerialName("medium_url") val mediumUrl: String? = null,
    @SerialName("small_url") val smallUrl: String? = null,
    val thumbnail: String? = null,
)

@Serializable
data class HardinessColumn(
    val min: String?,
    val max: String?,
)

@Serializable
data class PruningCountColumn(
    val amount: Double,
    val interval: String,
)

@Serializable
data class AnatomyColumn(
    val part: String?,
    val color: List<String>?,
)

@Serializable
data class WateringBenchmarkColumn(
    val value: String?,
    val unit: String?
)

@Serializable
data class DimensionsColumn(
    val type: String?,
    val minValue: Double?,
    val maxValue: Double?,
    val unit: String?
)
