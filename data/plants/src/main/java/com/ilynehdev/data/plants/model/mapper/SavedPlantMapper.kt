package com.ilynehdev.data.plants.model.mapper

import com.ilynehdev.core.database.projections.SavedPlantsRow
import com.ilynehdev.data.plants.model.PlantId
import com.ilynehdev.data.plants.model.SavedPlant


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
