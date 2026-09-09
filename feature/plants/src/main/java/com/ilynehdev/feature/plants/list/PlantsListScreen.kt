package com.ilynehdev.feature.plants.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.ilynehdev.feature.plants.ui.components.SearchTextField
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlantListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlantsListViewModel = koinViewModel(),
) {
    val plants = viewModel.plants.collectAsLazyPagingItems()
    val searchQuery by viewModel.query.collectAsStateWithLifecycle()
    PlantListContent(
        plants = plants,
        searchQuery = searchQuery,
        onSearchQueryChanged = {
            viewModel.onQueryChanged(it)
        },
        onFilterClicked = {
            // nave to filter
        },
        modifier = modifier
    )
}

@Composable
fun PlantListContent(
    plants: LazyPagingItems<PlantsListUiData>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MainHeader(text = stringResource(R.string.plants))

        Row(
            modifier = Modifier.height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SearchTextField(
                value = searchQuery,
                placeHolderText = stringResource(R.string.search_plants),
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.weight(1f)
            )

            OutlinedIconButton(
                onClick = onFilterClicked,
                shape = RoundedCornerShape(16.dp),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(R.string.filter)
                )
            }
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = plants.itemCount, key = plants.itemKey { it.id }) { index ->
                val plant = plants[index]
                PlantsListItem(
                    commonName = plant?.commonName.orEmpty(),
                    scientificName = plant?.scientificName,
                    imageUrl = plant?.imageUrl,
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
        onFilterClicked = {}
    )
}
