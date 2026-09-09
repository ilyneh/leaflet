package com.ilynehdev.feature.plants.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.ilynehdev.feature.plants.R
import com.ilynehdev.feature.plants.ui.components.MainHeader
import com.ilynehdev.feature.plants.ui.components.SearchFilterBar
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantListScreen(
    onPlantClicked: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantsListViewModel = koinViewModel(),
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    val searchQuery by viewModel.query.collectAsStateWithLifecycle()
    PlantListContent(
        plants = plants,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onQueryChanged,
        onFilterClicked = {
            // nav to filter
        },
        onItemClicked = onPlantClicked,
        modifier = modifier
    )
}

@Composable
fun PlantListContent(
    plants: LazyPagingItems<PlantsListUiData>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
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
fun PlantListContentPreview() {
    val plants = flowOf(
        PagingData.from(
            listOf(
                PlantsListUiData(1, "Monstera Deliciosa", "Monstera deliciosa", null),
                PlantsListUiData(2, "Snake Plant", "null", null),
                PlantsListUiData(3, "Aloe Vera", null, null,),
            )
        )
    ).collectAsLazyPagingItems()

    PlantListContent(
        plants = plants,
        searchQuery = "",
        onSearchQueryChanged = {},
        onFilterClicked = {},
        onItemClicked = {}
    )
}
