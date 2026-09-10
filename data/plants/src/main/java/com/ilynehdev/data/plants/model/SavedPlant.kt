package com.ilynehdev.data.plants.model

data class SavedPlant(
    val id: Long,
    val commonName: String?,
    val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val thumbnail: String?,
    val savedAt: Long,
)
