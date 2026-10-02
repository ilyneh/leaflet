package com.ilynehdev.feature.plants.data.model.mapper

import com.ilynehdev.core.database.projections.SavedPlantsRow
import com.ilynehdev.feature.plants.data.model.PlantId
import com.ilynehdev.feature.plants.data.model.SavedPlant


internal fun List<SavedPlantsRow>.toSavedPlants(): List<SavedPlant> =
    map { it.toSavedPlant() }

internal fun SavedPlantsRow.toSavedPlant() =
    SavedPlant(
        id = PlantId(id),
        commonName = commonName,
        scientificName = scientificName,
        watering = watering,
        sunlight = sunlight,
        thumbnail = thumbnail,
        savedAt = savedAt
    )
