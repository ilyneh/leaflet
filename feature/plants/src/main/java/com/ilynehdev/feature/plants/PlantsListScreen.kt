package com.ilynehdev.feature.plants

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ilynehdev.data.plants.model.Plant
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlantsViewModel = koinViewModel()
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    PlantListContent(plants, modifier)
}

@Composable
fun PlantListContent(
    plants: LazyPagingItems<Plant>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(count = plants.itemCount, key = plants.itemKey { it.id }) { index ->
            val plant = plants[index]
            Text(
                text = plant?.commonName.orEmpty(),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlantListContentPreview() {
    val plants = flowOf(
        PagingData.from(
            listOf(
                Plant(1, "Monstera Deliciosa", null, null, null, null),
                Plant(2, "Snake Plant", null, null, null, null),
                Plant(3, "Aloe Vera", null, null, null, null),
            )
        )
    ).collectAsLazyPagingItems()

    PlantListContent(plants = plants)
}