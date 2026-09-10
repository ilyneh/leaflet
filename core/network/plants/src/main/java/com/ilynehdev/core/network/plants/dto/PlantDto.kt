package com.ilynehdev.core.network.plants.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull

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

// The API sends pruning_count as [] when absent and as an object when present.
object PruningCountDtoSerializer : KSerializer<PlantPruningCountDto?> {
    override val descriptor: SerialDescriptor =
        PlantPruningCountDto.serializer().nullable.descriptor

    override fun deserialize(decoder: Decoder): PlantPruningCountDto? {
        val input = decoder as JsonDecoder
        return when (val element = input.decodeJsonElement()) {
            is JsonNull -> null
            is JsonArray -> element.firstOrNull()?.let {
                input.json.decodeFromJsonElement(PlantPruningCountDto.serializer(), it)
            }
            else -> input.json.decodeFromJsonElement(PlantPruningCountDto.serializer(), element)
        }
    }

    override fun serialize(encoder: Encoder, value: PlantPruningCountDto?) {
        if (value == null) {
            (encoder as JsonEncoder).encodeJsonElement(JsonNull)
        } else {
            encoder.encodeSerializableValue(PlantPruningCountDto.serializer(), value)
        }
    }
}

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
    val genus: String? = null,
    // Server quirk: sometimes holds the quoted cultivar instead of a real epithet
    // (e.g. id 10 has species_epithet "'Johin'", cultivar "Johin"). Do not derive
    // the scientific name from genus + speciesEpithet; use scientificName.
    @SerialName("species_epithet") val speciesEpithet: String? = null,
    val cultivar: String? = null,
    val variety: String? = null,
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
    @Serializable(with = PruningCountDtoSerializer::class)
    @SerialName("pruning_count") val pruningCount: PlantPruningCountDto? = null,
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


