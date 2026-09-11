package com.ilynehdev.feature.plants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.model.SavedPlant
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepository
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
)

data class PlantsListUiItem(
    val id: Long,
    val commonName: String,
    val scientificName: String?,
    val imageUrl: String?,
)

class PlantsListViewModel(
    private val pagedPlantsRepository: PagedPlantsRepository,
    private val plantsRepository: PlantsRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val showSavedOnly = MutableStateFlow(false)

    val uiState: StateFlow<PlantsListUiState> = combine(query, showSavedOnly) { query, showSavedOnly ->
        PlantsListUiState(query, showSavedOnly)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlantsListUiState())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val plants: Flow<PagingData<PlantsListUiItem>> = combine(
        query.debounce(300.milliseconds).distinctUntilChanged(),
        showSavedOnly,
    ) { query, showSavedOnly -> query to showSavedOnly }
        .flatMapLatest { (query, showSavedOnly) ->
            when {
                showSavedOnly -> plantsRepository.observeSavedPlants()
                    .map {  savedPlants ->
                        PagingData.from(savedPlants.map { it.toUiItem() })
                    }
                query.isBlank() -> pagedPlantsRepository.observePlants().map { pagingData ->
                    pagingData.map { it.toUiItem() }
                }
                else -> pagedPlantsRepository.searchPlant(query.trim()).map { pagingData ->
                    pagingData.map { it.toUiItem() }
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

    private fun SavedPlant.toUiItem() = PlantsListUiItem(
        id = id,
        commonName = commonName.orEmpty(),
        scientificName = scientificName?.firstOrNull(),
        imageUrl = thumbnail
    )

    private fun Plant.toUiItem() = PlantsListUiItem(
        id = id,
        commonName = commonName.orEmpty(),
        scientificName = scientificName?.firstOrNull(),
        imageUrl = thumbnail
    )
}
