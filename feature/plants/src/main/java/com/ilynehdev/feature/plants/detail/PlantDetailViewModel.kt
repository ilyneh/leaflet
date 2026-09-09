package com.ilynehdev.feature.plants.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.repository.PlantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LoadingStatus {
    data object Loading : LoadingStatus
    data object Done : LoadingStatus
    data class Failed(val error: FetchError) : LoadingStatus
}

data class PlantDetailUiState(
    val plant: Plant? = null,
    val loadingStatus: LoadingStatus = LoadingStatus.Loading,
)

class PlantDetailViewModel(
    private val plantId: Long,
    private val repository: PlantsRepository,
) : ViewModel() {

    private val loadingStatus = MutableStateFlow<LoadingStatus>(LoadingStatus.Loading)

    val uiState: StateFlow<PlantDetailUiState> = combine(
        repository.observePlant(plantId),
        loadingStatus,
    ) { plant, status ->
        PlantDetailUiState(plant = plant, loadingStatus = status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlantDetailUiState(),
    )

    init {
        refresh()
    }

    fun onRetryClicked() = refresh()

    private fun refresh() {
        viewModelScope.launch {
            loadingStatus.value = LoadingStatus.Loading
            loadingStatus.value = when (val error = repository.refreshPlantDetails(plantId)) {
                null -> LoadingStatus.Done
                else -> LoadingStatus.Failed(error)
            }
        }
    }
}
