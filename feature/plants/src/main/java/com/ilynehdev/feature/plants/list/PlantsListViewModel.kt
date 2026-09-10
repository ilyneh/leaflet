package com.ilynehdev.feature.plants.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds


data class PlantsListUiData(
    val id: Long,
    val commonName: String,
    val scientificName: String?,
    val imageUrl: String?,
)

class PlantsListViewModel(
    private val repository: PagedPlantsRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val plants: Flow<PagingData<PlantsListUiData>> = _query
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { data ->
            if (data.isBlank()) repository.observePlants() else repository.searchPlant(data.trim())
        }
        .map { data ->
            data.map { plant ->
                PlantsListUiData(
                    id = plant.id,
                    commonName = plant.commonName.orEmpty(),
                    scientificName = plant.scientificName?.first(),
                    imageUrl = plant.thumbnail
                )
            }
        }
        .cachedIn(viewModelScope)   // must be the last operator
}
