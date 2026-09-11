package com.ilynehdev.data.plants.model

data class Plant(
    val id: Long,
    val commonName: String?,
    val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val thumbnail: String?,
    // Filter inputs; null on rows built from the sparse summary projection.
    val wateringBenchmark: WateringBenchmark? = null,
    val careLevel: String? = null,
    val poisonousToHumans: Boolean? = null,
    val poisonousToPets: Boolean? = null,
    val dimensions: List<Dimension>? = null,
)
