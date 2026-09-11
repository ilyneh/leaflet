package com.ilynehdev.feature.plants.list

import androidx.paging.PagingData
import com.ilynehdev.data.plants.filters.FilterQueryParams
import com.ilynehdev.data.plants.filters.LightFilter
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.usecase.ObserveFilteredPlantsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlantsListViewModelTest {

    private class FakePagedPlantsRepository : PagedPlantsRepository {
        var mediatorCollections = 0
        val filteredRequests = mutableListOf<Triple<String, Boolean, FilterQueryParams>>()
        val filteredPlants = MutableStateFlow<List<Plant>>(emptyList())

        override fun observePlants(): Flow<PagingData<Plant>> = flow {
            mediatorCollections++
            emit(PagingData.empty())
        }

        override fun observeFilteredPlants(
            query: String,
            savedOnly: Boolean,
            searchParams: FilterQueryParams,
        ): Flow<List<Plant>> {
            filteredRequests += Triple(query, savedOnly, searchParams)
            return filteredPlants
        }
    }

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakePagedPlantsRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() =
        PlantsListViewModel(repository, ObserveFilteredPlantsUseCase(repository))

    // The query debounce delays the first routing decision; step past it.
    private fun kotlinx.coroutines.test.TestScope.settle() {
        advanceTimeBy(301)
        runCurrent()
    }

    @Test
    fun `default state routes to the mediator browse`() = runTest {
        val vm = viewModel()
        backgroundScope.launch { vm.plants.collect {} }

        settle()

        assertEquals(1, repository.mediatorCollections)
        assertTrue(repository.filteredRequests.isEmpty())
    }

    @Test
    fun `setting a filter switches to the filtered branch`() = runTest {
        val vm = viewModel()
        backgroundScope.launch { vm.plants.collect {} }
        backgroundScope.launch { vm.uiState.collect {} }
        settle()

        vm.onFiltersChanged(
            PlantFilters(light = setOf(LightFilter.LowLight, LightFilter.DirectSun)),
        )
        settle()

        assertEquals(Triple("", false, FilterQueryParams()), repository.filteredRequests.single())
        // two selections in one trait count as one active filter
        assertEquals(1, vm.uiState.value.filters.activeCount)
    }

    @Test
    fun `clearing filters with blank query returns to the mediator`() = runTest {
        val vm = viewModel()
        backgroundScope.launch { vm.plants.collect {} }
        settle()

        vm.onFiltersChanged(PlantFilters(safety = setOf(SafetyFilter.PetSafe)))
        settle()
        vm.onFiltersChanged(PlantFilters())
        settle()

        assertEquals(2, repository.mediatorCollections)
    }

    @Test
    fun `query saved and filters compose into one filtered call`() = runTest {
        val vm = viewModel()
        backgroundScope.launch { vm.plants.collect {} }
        settle()

        val filters = PlantFilters(
            safety = setOf(SafetyFilter.PetSafe, SafetyFilter.NonToxicToChildren),
        )
        vm.onQueryChanged("  fern ")
        vm.toggleShowSavedOnly()
        vm.onFiltersChanged(filters)
        settle()

        assertEquals(
            Triple("fern", true, FilterQueryParams(poisonous = false)),
            repository.filteredRequests.last(),
        )
    }

    @Test
    fun `onFiltersChanged is reflected in uiState`() = runTest {
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }
        runCurrent()

        vm.onFiltersChanged(PlantFilters(light = setOf(LightFilter.Medium)))
        runCurrent()

        assertEquals(
            PlantsListFiltersUiData(
                activeCount = 1,
                selectedLight = setOf(LightFilter.Medium),
            ),
            vm.uiState.value.filters,
        )
    }
}
