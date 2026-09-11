package com.ilynehdev.feature.plants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilynehdev.data.plants.filters.CareLevelFilter
import com.ilynehdev.data.plants.filters.LightFilter
import com.ilynehdev.data.plants.filters.MatureSizeFilter
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.data.plants.filters.WateringFilter
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.usecase.ObserveFilteredPlantsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

data class PlantsListUiState(
    val query: String = "",
    val showSavedOnly: Boolean = false,
    val filters: PlantsListFiltersUiData = PlantsListFiltersUiData(),
)

data class PlantsListFiltersUiData(
    val activeCount: Int = 0,
    val selectedLight: Set<LightFilter> = emptySet(),
    val selectedWatering: Set<WateringFilter> = emptySet(),
    val selectedSafety: Set<SafetyFilter> = emptySet(),
    val selectedCareLevel: Set<CareLevelFilter> = emptySet(),
    val selectedMatureSize: Set<MatureSizeFilter> = emptySet(),
)

data class PlantsListItemUiData(
    val id: Long,
    val commonName: String,
    val scientificName: String?,
    val imageUrl: String?,
)

class PlantsListViewModel(
    private val pagedPlantsRepository: PagedPlantsRepository,
    private val observeFilteredPlants: ObserveFilteredPlantsUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val showSavedOnly = MutableStateFlow(false)
    private val filters = MutableStateFlow(PlantFilters())

    val uiState: StateFlow<PlantsListUiState> =
        combine(query, showSavedOnly, filters) { query, showSavedOnly, filters ->
            PlantsListUiState(query, showSavedOnly, filters.toUiData())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlantsListUiState())

    // Only the query is debounced; chip and filter changes apply instantly.
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val plants: Flow<PagingData<PlantsListItemUiData>> = combine(
        query.debounce(300.milliseconds).distinctUntilChanged(),
        showSavedOnly,
        filters,
    ) { query, showSavedOnly, filters -> Triple(query, showSavedOnly, filters) }
        .flatMapLatest { (query, showSavedOnly, filters) ->
            if (query.isBlank() && !showSavedOnly && filters.isEmpty) {
                pagedPlantsRepository.observePlants().map { pagingData ->
                    pagingData.map { it.toUiItem() }
                }
            } else {
                observeFilteredPlants(query.trim(), showSavedOnly, filters).map { plants ->
                    PagingData.from(plants.map { it.toUiItem() })
                }
            }
        }
        .cachedIn(viewModelScope)   // must be the last operator

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun toggleShowSavedOnly() {
        showSavedOnly.update { !it }
    }

    fun onFiltersChanged(value: PlantFilters) {
        filters.value = value
    }

    private fun PlantFilters.toUiData() = PlantsListFiltersUiData(
        activeCount = activeCount,
        selectedLight = light,
        selectedWatering = watering,
        selectedSafety = safety,
        selectedCareLevel = careLevel,
        selectedMatureSize = matureSize,
    )

    private fun Plant.toUiItem() = PlantsListItemUiData(
        id = id,
        commonName = commonName.orEmpty(),
        scientificName = scientificName?.firstOrNull(),
        imageUrl = thumbnail
    )
}
