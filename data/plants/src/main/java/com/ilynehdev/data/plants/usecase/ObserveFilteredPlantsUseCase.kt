package com.ilynehdev.data.plants.usecase

import com.ilynehdev.data.plants.filters.FilterMapper
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.toQueryParams
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * SQL narrows by query and saved membership (in the repository); the filter
 * predicates run here in memory — sunlight and dimensions are converter-stored
 * and watering needs benchmark parsing, none of it SQL-expressible. Server
 * params only pre-narrow the search fetch; these predicates are the final
 * arbiter on everything emitted.
 */
class ObserveFilteredPlantsUseCase(
    private val repository: PagedPlantsRepository,
) {
    operator fun invoke(
        query: String,
        savedOnly: Boolean,
        filters: PlantFilters,
    ): Flow<List<Plant>> =
        repository.observeFilteredPlants(query, savedOnly, filters.toQueryParams())
            .map { plants -> plants.filter { it.matches(filters) } }

    private fun Plant.matches(filters: PlantFilters): Boolean =
        FilterMapper.matchesLight(sunlight, filters.light) &&
            FilterMapper.matchesWatering(
                benchmarkValue = wateringBenchmark?.value,
                benchmarkUnit = wateringBenchmark?.unit,
                watering = watering,
                selected = filters.watering,
            ) &&
            FilterMapper.matchesSafety(poisonousToPets, poisonousToHumans, filters.safety) &&
            FilterMapper.matchesCareLevel(careLevel, filters.careLevel) &&
            FilterMapper.matchesMatureSize(dimensions, filters.matureSize)
}
