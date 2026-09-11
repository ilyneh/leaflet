package com.ilynehdev.feature.plants.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilynehdev.data.common.RefreshResult
import com.ilynehdev.data.plants.model.Dimension
import com.ilynehdev.data.plants.model.PlantDetails
import com.ilynehdev.data.plants.repository.PlantsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

sealed interface LoadingStatus {
    data object Loading : LoadingStatus
    data object Done : LoadingStatus
    data object Failed : LoadingStatus
}

data class PlantsDetailUiData(
    val commonName: String,
    val latinName: String?,
    val imageUrl: String?,
    val description: String?,
    val lightLabel: String?,
    val waterLabel: String?,
    val matureSize: String?,
    val careLevel: String?,
    val toxicToPets: Boolean?,
    val toxicityNotice: ToxicityNotice?
) {
    enum class ToxicityNotice { PetsAndHumans, Pets, Humans }
}

data class PlantsDetailUiState(
    val plant: PlantsDetailUiData? = null,
    val saveButtonVisible: Boolean = false,
    val isSaved: Boolean = false,
    val loadingStatus: LoadingStatus = LoadingStatus.Loading,
)

class PlantsDetailViewModel(
    private val plantId: Long,
    private val plantsRepository: PlantsRepository,
) : ViewModel() {

    private val loadingStatus = MutableStateFlow<LoadingStatus>(LoadingStatus.Loading)

    val uiState: StateFlow<PlantsDetailUiState> = combine(
        plantsRepository.observePlant(plantId),
        plantsRepository.observeIsSaved(plantId),
        loadingStatus,
    ) { plant, isSaved, status ->
        PlantsDetailUiState(
            plant = plant?.toUiData(),
            saveButtonVisible = plant != null,
            isSaved = isSaved,
            loadingStatus = status
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlantsDetailUiState(),
    )

    init {
        refresh()
    }

    fun onPullToRefresh() = refresh(force = true)

    fun onSaveClicked() {
        viewModelScope.launch {
            try {
                plantsRepository.updateSavedPlant(plantId, !uiState.value.isSaved)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                }
                // swallow
            }
        }
    }

    fun onRetryClicked() = refresh()

    private fun refresh(force: Boolean = false) {
        viewModelScope.launch {
            loadingStatus.value = LoadingStatus.Loading
            loadingStatus.value = when (plantsRepository.refreshPlantDetails(plantId, force)) {
                is RefreshResult.Refreshed,
                is RefreshResult.AlreadyFresh -> LoadingStatus.Done
                is RefreshResult.Failed -> LoadingStatus.Failed
            }
        }
    }
}

private fun PlantDetails.toUiData() = PlantsDetailUiData(
    commonName = commonName.orEmpty(),
    latinName = scientificName?.firstOrNull(),
    imageUrl = imageUrl,
    description = description,
    lightLabel = sunlight?.firstOrNull()?.titleCase(),
    waterLabel = wateringBenchmark
        ?.takeIf { it.value != null && it.unit != null }
        ?.let { "Every ${it.value} ${it.unit}" }
        ?: watering?.titleCase(),
    matureSize = dimensions?.firstOrNull()?.toLabel(),
    careLevel = careLevel?.titleCase(),
    toxicToPets = poisonousToPets,
    toxicityNotice = when {
        poisonousToPets == true && poisonousToHumans == true ->
            PlantsDetailUiData.ToxicityNotice.PetsAndHumans
        poisonousToPets == true -> PlantsDetailUiData.ToxicityNotice.Pets
        poisonousToHumans == true -> PlantsDetailUiData.ToxicityNotice.Humans
        else -> null
    }
)

private fun Dimension.toLabel(): String? {
    val unit = unit ?: return null
    val min = minValue?.trimmed()
    val max = maxValue?.trimmed()
    return when {
        min != null && max != null && min != max -> "$min–$max $unit"
        max != null -> "$max $unit"
        min != null -> "$min $unit"
        else -> null
    }
}

private fun Double.trimmed(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

private fun String.titleCase(): String =
    replaceFirstChar { it.titlecase(Locale.getDefault()) }
