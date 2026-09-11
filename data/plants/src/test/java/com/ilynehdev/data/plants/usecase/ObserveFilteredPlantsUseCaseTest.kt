package com.ilynehdev.data.plants.usecase

import androidx.paging.PagingData
import com.ilynehdev.data.plants.filters.FilterQueryParams
import com.ilynehdev.data.plants.filters.LightFilter
import com.ilynehdev.data.plants.filters.MatureSizeFilter
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.data.plants.filters.WateringFilter
import com.ilynehdev.data.plants.model.Dimension
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.model.WateringBenchmark
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveFilteredPlantsUseCaseTest {

    private class FakePagedPlantsRepository : PagedPlantsRepository {
        var plants: List<Plant> = emptyList()
        val requests = mutableListOf<Triple<String, Boolean, FilterQueryParams>>()

        override fun observeFilteredPlants(
            query: String,
            savedOnly: Boolean,
            searchParams: FilterQueryParams,
        ): Flow<List<Plant>> {
            requests += Triple(query, savedOnly, searchParams)
            return flowOf(plants)
        }

        override fun observePlants(): Flow<PagingData<Plant>> = flowOf(PagingData.empty())
    }

    private val repo = FakePagedPlantsRepository()
    private val useCase = ObserveFilteredPlantsUseCase(repo)

    private fun plant(
        id: Long,
        sunlight: List<String>? = null,
        benchmark: WateringBenchmark? = null,
        watering: String? = null,
        poisonousToPets: Boolean? = null,
        poisonousToHumans: Boolean? = null,
        careLevel: String? = null,
        dimensions: List<Dimension>? = null,
    ) = Plant(
        id = id,
        commonName = "Plant $id",
        scientificName = null,
        watering = watering,
        sunlight = sunlight,
        thumbnail = null,
        wateringBenchmark = benchmark,
        careLevel = careLevel,
        poisonousToHumans = poisonousToHumans,
        poisonousToPets = poisonousToPets,
        dimensions = dimensions,
    )

    @Test
    fun `passes query savedOnly and computed params to the repository`() = runTest {
        val filters = PlantFilters(
            light = setOf(LightFilter.DirectSun),
            safety = setOf(SafetyFilter.PetSafe, SafetyFilter.NonToxicToChildren),
        )

        useCase("fern", savedOnly = true, filters = filters).first()

        assertEquals(
            Triple("fern", true, FilterQueryParams(sunlight = "full_sun", poisonous = false)),
            repo.requests.single(),
        )
    }

    @Test
    fun `empty filters emit everything the repository returned`() = runTest {
        repo.plants = listOf(plant(1), plant(2))

        val items = useCase("", savedOnly = false, filters = PlantFilters()).first()

        assertEquals(listOf(1L, 2L), items.map { it.id })
    }

    @Test
    fun `a single active filter narrows and excludes unknown data`() = runTest {
        repo.plants = listOf(
            plant(1, sunlight = listOf("full sun")),
            plant(2, sunlight = listOf("full shade")),
            plant(3),   // unknown sunlight never matches
        )

        val filters = PlantFilters(light = setOf(LightFilter.DirectSun))
        val items = useCase("", savedOnly = false, filters = filters).first()

        assertEquals(listOf(1L), items.map { it.id })
    }

    @Test
    fun `categories compose with AND`() = runTest {
        repo.plants = listOf(
            plant(1, sunlight = listOf("full sun"), benchmark = WateringBenchmark("7", "days")),
            plant(2, sunlight = listOf("full sun"), benchmark = WateringBenchmark("30", "days")),
            plant(3, sunlight = listOf("full shade"), benchmark = WateringBenchmark("7", "days")),
        )

        val filters = PlantFilters(
            light = setOf(LightFilter.DirectSun),
            watering = setOf(WateringFilter.Weekly),
        )
        val items = useCase("", savedOnly = false, filters = filters).first()

        assertEquals(listOf(1L), items.map { it.id })
    }

    @Test
    fun `safety requires explicit false on both flags when both selected`() = runTest {
        repo.plants = listOf(
            plant(1, poisonousToPets = false, poisonousToHumans = false),
            plant(2, poisonousToPets = false, poisonousToHumans = null),
            plant(3, poisonousToPets = true, poisonousToHumans = false),
        )

        val filters = PlantFilters(
            safety = setOf(SafetyFilter.PetSafe, SafetyFilter.NonToxicToChildren),
        )
        val items = useCase("", savedOnly = false, filters = filters).first()

        assertEquals(listOf(1L), items.map { it.id })
    }

    @Test
    fun `mature size filters on converted dimensions`() = runTest {
        repo.plants = listOf(
            plant(1, dimensions = listOf(Dimension("Height", null, 8.0, "feet"))),
            plant(2, dimensions = listOf(Dimension("Height", null, 0.5, "feet"))),
        )

        val filters = PlantFilters(matureSize = setOf(MatureSizeFilter.Tall))
        val items = useCase("", savedOnly = false, filters = filters).first()

        assertEquals(listOf(1L), items.map { it.id })
    }

    @Test
    fun `all selections empty means no filtering not no results`() = runTest {
        repo.plants = listOf(plant(1))

        val items = useCase("aloe", savedOnly = false, filters = PlantFilters()).first()

        assertTrue(items.isNotEmpty())
        assertEquals(FilterQueryParams(), repo.requests.single().third)
    }
}
