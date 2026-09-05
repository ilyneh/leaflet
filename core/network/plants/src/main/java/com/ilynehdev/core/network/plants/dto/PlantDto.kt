package com.ilynehdev.core.network.plants.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantDimensionsDto(
    val type: String?,
    @SerialName("min_value") val minValue: Double?,
    @SerialName("max_value") val maxValue: Double?,
    val unit: String?,
)

@Serializable
enum class PlantCycleDto {
    @SerialName("Perennial") PERENNIAL,
    @SerialName("Annual") ANNUAL,
    @SerialName("Biennial") BIENNIAL,
    @SerialName("Biannual") BIANNUAL,
    UNKNOWN,
}

@Serializable
enum class PlantWateringDto {
    @SerialName("Frequent") FREQUENT,
    @SerialName("Average") AVERAGE,
    @SerialName("Minimum") MINIMUM,
    @SerialName("None") NONE,
    UNKNOWN,
}

@Serializable
data class PlantWateringGeneralBenchmarkDto(
    val value: String?,
    val unit: String?,
)


@Serializable
data class PlantAnatomyDto(
    val part: String,
    val color: List<String>,
)

@Serializable
data class PlantPruningCountDto(
    val amount: Double,
    val interval: String,
)

@Serializable
data class PlantHardinessDto(
    val min: String?,
    val max: String?,
)

@Serializable
data class PlantImageDto(
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
data class PlantDto(
    val id: Long,
    @SerialName("common_name") val commonName: String? = null,
    @SerialName("scientific_name") val scientificName: List<String>? = null,
    @SerialName("other_name") val otherName: List<String>? = null,
    val family: String? = null,
    val origin: List<String>? = null,
    val type: String? = null,
    val dimensions: List<PlantDimensionsDto>? = null,
    val cycle: PlantCycleDto = PlantCycleDto.UNKNOWN,
    val watering: PlantWateringDto = PlantWateringDto.UNKNOWN,
    @SerialName("watering_general_benchmark")
    val wateringGeneralBenchmark: PlantWateringGeneralBenchmarkDto? = null,
    @SerialName("plant_anatomy") val plantAnatomy: List<PlantAnatomyDto>? = null,
    val sunlight: List<String>? = null,
    @SerialName("pruning_month") val pruningMonth: List<String>? = null,
    @SerialName("pruning_count") val pruningCount: List<PlantPruningCountDto>? = null,
    val seeds: Boolean? = null,
    val attracts: List<String>? = null,
    val propagation: List<String>? = null,
    val hardiness: PlantHardinessDto? = null,
    val flowers: Boolean? = null,
    @SerialName("flowering_season") val floweringSeason: String? = null,
    val soil: List<String>? = null,
    @SerialName("pest_susceptibility") val pestSusceptibility: List<String>? = null,
    @SerialName("growth_rate") val growthRate: String? = null,
    val maintenance: String? = null,
    val medicinal: Boolean? = null,
    @SerialName("poisonous_to_humans") val poisonousToHumans: Boolean? = null,
    @SerialName("poisonous_to_pets") val poisonousToPets: Boolean? = null,
    @SerialName("drought_tolerant") val droughtTolerant: Boolean? = null,
    @SerialName("salt_tolerant") val saltTolerant: Boolean? = null,
    val thorny: Boolean? = null,
    val invasive: Boolean? = null,
    val rare: Boolean? = null,
    val tropical: Boolean? = null,
    val cuisine: Boolean? = null,
    val indoor: Boolean? = null,
    @SerialName("care_level") val careLevel: String? = null,
    val description: String? = null,
    @SerialName("default_image") val defaultImage: PlantImageDto? = null,
)


