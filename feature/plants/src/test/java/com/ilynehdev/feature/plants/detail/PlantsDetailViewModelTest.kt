package com.ilynehdev.feature.plants.detail

import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.data.common.RefreshResult
import com.ilynehdev.data.plants.model.PlantDetails
import com.ilynehdev.data.plants.model.SavedPlant
import com.ilynehdev.data.plants.repository.PlantsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlantsDetailViewModelTest {

    private class FakePlantsRepository : PlantsRepository {
        val plants = MutableStateFlow<PlantDetails?>(null)
        val savedIds = MutableStateFlow<Set<Long>>(emptySet())
        var refreshResult: RefreshResult = RefreshResult.Refreshed
        val refreshCalls = mutableListOf<Boolean>() // force flag per call
        var failSaveWith: Exception? = null

        override fun observePlant(plantId: Long): Flow<PlantDetails?> = plants

        override fun observeIsSaved(plantId: Long): Flow<Boolean> =
            savedIds.map { plantId in it }

        override fun observeSavedPlants(): Flow<List<SavedPlant>> =
            error("not used in these tests")

        override suspend fun updateSavedPlant(plantId: Long, saved: Boolean) {
            failSaveWith?.let { throw it }
            savedIds.value = if (saved) savedIds.value + plantId else savedIds.value - plantId
        }

        override suspend fun refreshPlantDetails(plantId: Long, force: Boolean): RefreshResult {
            refreshCalls += force
            return refreshResult
        }
    }

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakePlantsRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = PlantsDetailViewModel(PLANT_ID, repository)

    private fun details(commonName: String = "Monstera") = PlantDetails(
        id = PLANT_ID,
        commonName = commonName,
        scientificName = listOf("Monstera deliciosa"),
        description = null,
        imageUrl = null,
        watering = null,
        wateringBenchmark = null,
        sunlight = null,
        careLevel = null,
        cycle = null,
        poisonousToHumans = null,
        poisonousToPets = null,
        dimensions = null,
        indoor = null,
    )

    @Test
    fun `refreshes on init without force`() = runTest {
        viewModel()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(false), repository.refreshCalls)
    }

    @Test
    fun `uiState maps plant and marks Done on successful refresh`() = runTest {
        repository.plants.value = details()
        val vm = viewModel()

        val state = vm.uiState.first { it.plant != null && it.loadingStatus == LoadingStatus.Done }

        assertEquals("Monstera", state.plant?.commonName)
        assertTrue(state.saveButtonVisible)
    }

    @Test
    fun `failed refresh with no cached plant exposes Failed and hides save button`() = runTest {
        repository.refreshResult = RefreshResult.Failed(FetchError.Offline)
        val vm = viewModel()

        val state = vm.uiState.first { it.loadingStatus == LoadingStatus.Failed }

        assertNull(state.plant)
        assertFalse(state.saveButtonVisible)
    }

    @Test
    fun `failed refresh keeps cached plant visible`() = runTest {
        repository.plants.value = details()
        repository.refreshResult = RefreshResult.Failed(FetchError.Offline)
        val vm = viewModel()

        val state = vm.uiState.first { it.loadingStatus == LoadingStatus.Failed }

        assertEquals("Monstera", state.plant?.commonName)
    }

    @Test
    fun `AlreadyFresh maps to Done`() = runTest {
        repository.plants.value = details()
        repository.refreshResult = RefreshResult.AlreadyFresh
        val vm = viewModel()

        val state = vm.uiState.first { it.loadingStatus != LoadingStatus.Loading }

        assertEquals(LoadingStatus.Done, state.loadingStatus)
    }

    @Test
    fun `onPullToRefresh forces the fetch`() = runTest {
        val vm = viewModel()
        dispatcher.scheduler.advanceUntilIdle()

        vm.onPullToRefresh()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(false, true), repository.refreshCalls)
    }

    @Test
    fun `onSaveClicked flips saved state both ways`() = runTest {
        repository.plants.value = details()
        val vm = viewModel()
        val collector = launch { vm.uiState.collect { } } // keep WhileSubscribed active

        vm.uiState.first { !it.isSaved }

        vm.onSaveClicked()
        vm.uiState.first { it.isSaved }

        vm.onSaveClicked()
        vm.uiState.first { !it.isSaved }

        collector.cancel()
    }

    @Test
    fun `save failure is swallowed and state stays unsaved`() = runTest {
        repository.plants.value = details()
        repository.failSaveWith = IllegalStateException("disk full")
        val vm = viewModel()
        val collector = launch { vm.uiState.collect { } }

        vm.uiState.first { it.plant != null }
        vm.onSaveClicked()
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.uiState.value.isSaved)

        collector.cancel()
    }

    companion object {
        const val PLANT_ID = 7L
    }
}
