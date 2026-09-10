package com.ilynehdev.feature.plants.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.data.plants.model.Dimension
import com.ilynehdev.data.plants.model.PlantDetails
import com.ilynehdev.data.plants.repository.PlantsRepository
import java.util.Locale
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

data class PlantDetailUiData(
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

data class PlantDetailUiState(
    val plant: PlantDetailUiData? = null,
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
        PlantDetailUiState(plant = plant?.toUiData(), loadingStatus = status)
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

private fun PlantDetails.toUiData() = PlantDetailUiData(
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
            PlantDetailUiData.ToxicityNotice.PetsAndHumans
        poisonousToPets == true -> PlantDetailUiData.ToxicityNotice.Pets
        poisonousToHumans == true -> PlantDetailUiData.ToxicityNotice.Humans
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
