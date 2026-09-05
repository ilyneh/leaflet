package com.ilynehdev.core.network.plants.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantDiseaseSectionDto(
    val subtitle: String? = null,
    val description: String? = null,
)

@Serializable
data class PlantDiseaseDto(
    val id: Long,
    @SerialName("common_name") val commonName: String? = null,
    @SerialName("scientific_name") val scientificName: String? = null,   // single string here, not list
    @SerialName("other_name") val otherName: List<String>? = null,
    val family: String? = null,
    val description: List<PlantDiseaseSectionDto>? = null,
    val solution: List<PlantDiseaseSectionDto>? = null,
    val host: List<String>? = null,
    val images: List<PlantImageDto>? = null,
)
