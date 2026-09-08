package com.ilynehdev.feature.plants.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ilynehdev.feature.plants.ui.components.MainHeader
import com.ilynehdev.feature.plants.ui.components.SearchTextField
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlantsListViewModel = koinViewModel()
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    PlantListContent(
        plants = plants,
        onSearchQueryChanged = {},
        modifier = modifier
    )
}

@Composable
fun PlantListContent(
    plants: LazyPagingItems<PlantsListUiData>,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MainHeader(text = "Plants")
        
        SearchTextField(
            value = "",
            placeHolderText = "Search plants",
            onValueChange = onSearchQueryChanged
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = plants.itemCount, key = plants.itemKey { it.id }) { index ->
                val plant = plants[index]
                PlantsListItem(
                    commonName = plant?.commonName.orEmpty(),
                    scientificName = plant?.scientificName,
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
        onSearchQueryChanged = { }
    )
}
