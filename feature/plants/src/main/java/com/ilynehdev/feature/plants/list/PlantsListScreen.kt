package com.ilynehdev.feature.plants.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.ilynehdev.feature.plants.R
import com.ilynehdev.feature.plants.ui.components.MainHeader
import com.ilynehdev.feature.plants.ui.components.SavedFilterChip
import com.ilynehdev.feature.plants.ui.components.SearchFilterBar
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantsListScreen(
    onPlantClicked: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantsListViewModel = koinViewModel(),
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PlantsListContent(
        plants = plants,
        searchQuery = uiState.query,
        showSavedOnly = uiState.showSavedOnly,
        onSearchQueryChanged = viewModel::onQueryChanged,
        onFilterClicked = {
            // nav to filter
        },
        toggleShowSaved = viewModel::toggleShowSavedOnly,
        onItemClicked = onPlantClicked,
        modifier = modifier
    )
}

@Composable
fun PlantsListContent(
    plants: LazyPagingItems<PlantsListItemUiData>,
    searchQuery: String,
    showSavedOnly: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    toggleShowSaved: () -> Unit,
    onItemClicked: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            MainHeader(text = stringResource(R.string.plants))
            SearchFilterBar(searchQuery, onSearchQueryChanged, onFilterClicked)
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
            items(count = plants.itemCount, key = plants.itemKey { it.id }) { index ->
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
}

@Preview(showBackground = true)
@Composable
fun PlantsListContentPreview() {
    val plants = flowOf(
        PagingData.from(
            listOf(
                PlantsListItemUiData(1, "Monstera Deliciosa", "Monstera deliciosa", null),
                PlantsListItemUiData(2, "Snake Plant", "null", null),
                PlantsListItemUiData(3, "Aloe Vera", null, null,),
            )
        )
    ).collectAsLazyPagingItems()

    LeafletTheme {
        PlantsListContent(
            plants = plants,
            searchQuery = "",
            showSavedOnly = true,
            onSearchQueryChanged = {},
            onFilterClicked = {},
            toggleShowSaved = {},
            onItemClicked = {}
        )
    }
}
