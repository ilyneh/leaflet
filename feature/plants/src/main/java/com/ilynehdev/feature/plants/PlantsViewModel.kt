package com.ilynehdev.feature.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.data.plants.repository.PlantsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.time.Duration.Companion.milliseconds

class PlantsViewModel(
    private val repository: PlantsRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val plants: Flow<PagingData<Plant>> = _query
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.isBlank()) repository.observePlants() else repository.searchPlant(q.trim())
        }
        .cachedIn(viewModelScope)   // must be the last operator
}
