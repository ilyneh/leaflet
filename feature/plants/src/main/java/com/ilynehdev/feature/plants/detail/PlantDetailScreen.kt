package com.ilynehdev.feature.plants.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.data.plants.model.Plant
import com.ilynehdev.feature.plants.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantDetailViewModel = koinViewModel(parameters = { parametersOf(plantId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PlantDetailContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onRetryClicked = viewModel::onRetryClicked,
        modifier = modifier,
    )
}

@Composable
fun PlantDetailContent(
    uiState: PlantDetailUiState,
    onBackClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
                .size(44.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_back),
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        when {
            uiState.plant != null -> PlantDetails(
                plant = uiState.plant,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
            )

            uiState.loadingStatus is LoadingStatus.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )

            else -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.plant_detail_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onRetryClicked) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun PlantDetails(
    plant: Plant,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = plant.commonName.orEmpty(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = plant.scientificName?.firstOrNull().orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantDetailContentPreview() {
    LeafletTheme {
        PlantDetailContent(
            uiState = PlantDetailUiState(
                plant = Plant(
                    id = 16L,
                    commonName = "Monstera",
                    scientificName = listOf("Monstera deliciosa"),
                    watering = null,
                    sunlight = null,
                    thumbnail = null,
                ),
            ),
            onBackClicked = {},
            onRetryClicked = {},
        )
    }
}
