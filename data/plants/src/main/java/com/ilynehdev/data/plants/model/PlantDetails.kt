package com.ilynehdev.data.plants.model

data class PlantDetails(
    val id: Long,
    val commonName: String?,
    val scientificName: List<String>?,
    val description: String?,
    val imageUrl: String?,
    val watering: String?,
    val wateringBenchmark: WateringBenchmark?,
    val sunlight: List<String>?,
    val careLevel: String?,
    val cycle: String?,
    val poisonousToHumans: Boolean?,
    val poisonousToPets: Boolean?,
    val dimensions: List<Dimension>?,
    val indoor: Boolean?,
)

data class WateringBenchmark(
    val value: String?,
    val unit: String?,
)

data class Dimension(
    val type: String?,
    val minValue: Double?,
    val maxValue: Double?,
    val unit: String?,
)
