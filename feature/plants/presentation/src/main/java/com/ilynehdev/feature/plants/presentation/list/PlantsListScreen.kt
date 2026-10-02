package com.ilynehdev.feature.plants.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.data.filters.LightFilter
import com.ilynehdev.feature.plants.data.filters.PlantFilters
import com.ilynehdev.feature.plants.data.filters.SafetyFilter
import com.ilynehdev.feature.plants.data.model.PlantId
import com.ilynehdev.feature.plants.presentation.R
import com.ilynehdev.feature.plants.presentation.ui.components.MainHeader
import com.ilynehdev.feature.plants.presentation.ui.components.SavedFilterChip
import com.ilynehdev.feature.plants.presentation.ui.components.SearchFilterBar
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantsListScreen(
    onPlantClicked: (id: PlantId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantsListViewModel = koinViewModel(),
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PlantsListContent(
        plants = plants,
        searchQuery = uiState.query,
        showSavedOnly = uiState.showSavedOnly,
        filters = uiState.filters,
        onSearchQueryChanged = viewModel::onQueryChanged,
        onFiltersApplied = viewModel::onFiltersChanged,
        toggleShowSaved = viewModel::toggleShowSavedOnly,
        onItemClicked = onPlantClicked,
        modifier = modifier
    )
}

@Composable
internal fun PlantsListContent(
    plants: LazyPagingItems<PlantsListItemUiData>,
    searchQuery: String,
    showSavedOnly: Boolean,
    filters: PlantsListFiltersUiData,
    onSearchQueryChanged: (String) -> Unit,
    onFiltersApplied: (PlantFilters) -> Unit,
    toggleShowSaved: () -> Unit,
    onItemClicked: (id: PlantId) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            MainHeader(text = stringResource(R.string.plants))
            SearchFilterBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onFilterClicked = { showFilterSheet = true },
                activeFilterCount = filters.activeCount,
            )
        }

        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SavedFilterChip(
                selected = showSavedOnly,
                onClick = toggleShowSaved
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp)
        ) {
            // Lazy keys must be Bundle-saveable; a boxed value class is not.
            items(count = plants.itemCount, key = plants.itemKey { it.id.value }) { index ->
                val plant = plants[index] ?: return@items
                PlantsListItem(
                    commonName = plant.commonName,
                    scientificName = plant.scientificName,
                    imageUrl = plant.imageUrl,
                    onItemClicked = { onItemClicked(plant.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showFilterSheet) {
        PlantsFilterSheet(
            current = filters,
            onApply = {
                onFiltersApplied(it)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun PlantsListContentPreview() {
    val plants = flowOf(
        PagingData.from(
            listOf(
                PlantsListItemUiData(PlantId(1), "Monstera Deliciosa", "Monstera deliciosa", null),
                PlantsListItemUiData(PlantId(2), "Snake Plant", "null", null),
                PlantsListItemUiData(PlantId(3), "Aloe Vera", null, null,),
            )
        )
    ).collectAsLazyPagingItems()

    LeafletTheme {
        PlantsListContent(
            plants = plants,
            searchQuery = "",
            showSavedOnly = true,
            filters = PlantsListFiltersUiData(
                activeCount = 2,
                selectedLight = setOf(LightFilter.LowLight),
                selectedSafety = setOf(SafetyFilter.PetSafe),
            ),
            onSearchQueryChanged = {},
            onFiltersApplied = {},
            toggleShowSaved = {},
            onItemClicked = {}
        )
    }
}
