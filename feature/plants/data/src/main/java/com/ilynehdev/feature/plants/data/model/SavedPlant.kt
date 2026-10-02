package com.ilynehdev.feature.plants.data.model

data class SavedPlant(
    val id: PlantId,
    val commonName: String?,
    val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val thumbnail: String?,
    val savedAt: Long,
)
