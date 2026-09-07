package com.ilynehdev.data.plants.mapper

import com.ilynehdev.core.database.entities.AnatomyColumn
import com.ilynehdev.core.database.entities.DimensionsColumn
import com.ilynehdev.core.database.entities.HardinessColumn
import com.ilynehdev.core.database.entities.ImageColumn
import com.ilynehdev.core.database.entities.PlantEntity
import com.ilynehdev.core.database.entities.PruningCountColumn
import com.ilynehdev.core.database.entities.WateringBenchmarkColumn
import com.ilynehdev.core.database.projections.PlantSummaryRow
import com.ilynehdev.core.network.plants.dto.PlantAnatomyDto
import com.ilynehdev.core.network.plants.dto.PlantDimensionsDto
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.network.plants.dto.PlantHardinessDto
import com.ilynehdev.core.network.plants.dto.PlantImageDto
import com.ilynehdev.core.network.plants.dto.PlantPruningCountDto
import com.ilynehdev.core.network.plants.dto.PlantWateringGeneralBenchmarkDto
import com.ilynehdev.data.plants.model.Plant

internal fun PlantDto.toEntity() = PlantEntity(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    otherName = otherName,
    family = family,
    genus = genus,
    speciesEpithet = speciesEpithet,
    cultivar = cultivar,
    variety = variety,
    origin = origin,
    type = type,
    dimensions = dimensions?.map { it.toColumn() },
    cycle = cycle.name,
    watering = watering.name,
    plantAnatomy = plantAnatomy?.map { it.toColumn() },
    sunlight = sunlight,
    pruningMonth = pruningMonth,
    pruningCount = pruningCount?.map { it.toColumn() },
    seeds = seeds,
    attracts = attracts,
    propagation = propagation,
    flowers = flowers,
    floweringSeason = floweringSeason,
    soil = soil,
    pestSusceptibility = pestSusceptibility,
    growthRate = growthRate,
    maintenance = maintenance,
    medicinal = medicinal,
    poisonousToHumans = poisonousToHumans,
    poisonousToPets = poisonousToPets,
    droughtTolerant = droughtTolerant,
    saltTolerant = saltTolerant,
    thorny = thorny,
    invasive = invasive,
    rare = rare,
    tropical = tropical,
    cuisine = cuisine,
    indoor = indoor,
    careLevel = careLevel,
    description = description,
    hardiness = hardiness?.toColumn(),
    wateringBenchmark = wateringGeneralBenchmark?.toColumn(),
    defaultImage = defaultImage?.toColumn(),
)

private fun PlantDimensionsDto.toColumn() = DimensionsColumn(
    type = type,
    minValue = minValue,
    maxValue = maxValue,
    unit = unit,
)

private fun PlantAnatomyDto.toColumn() = AnatomyColumn(
    part = part,
    color = color,
)

private fun PlantPruningCountDto.toColumn() = PruningCountColumn(
    amount = amount,
    interval = interval,
)

private fun PlantHardinessDto.toColumn() = HardinessColumn(
    min = min,
    max = max,
)

private fun PlantWateringGeneralBenchmarkDto.toColumn() = WateringBenchmarkColumn(
    value = value,
    unit = unit,
)

private fun PlantImageDto.toColumn() = ImageColumn(
    imageId = imageId,
    license = license,
    licenseName = licenseName,
    licenseUrl = licenseUrl,
    originalUrl = originalUrl,
    regularUrl = regularUrl,
    mediumUrl = mediumUrl,
    smallUrl = smallUrl,
    thumbnail = thumbnail,
)

internal fun PlantSummaryRow.toPlant() = Plant(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    watering = watering,
    sunlight = sunlight,
    thumbnail = thumbnail
)

internal fun PlantEntity.toPlant() = Plant(
    id = id,
    commonName = commonName,
    scientificName = scientificName,
    watering = watering,
    sunlight = sunlight,
    thumbnail = defaultImage?.thumbnail
)