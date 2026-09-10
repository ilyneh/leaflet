package com.ilynehdev.feature.plants.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ilynehdev.core.designsystem.LeafletTheme
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
        onSaveClicked = viewModel::onSaveClicked,
        onRetryClicked = viewModel::onRetryClicked,
        modifier = modifier,
    )
}

@Composable
fun PlantDetailContent(
    uiState: PlantDetailUiState,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.plant != null -> PlantDetails(
                plant = uiState.plant,
                modifier = Modifier.fillMaxSize(),
            )

            uiState.loadingStatus is LoadingStatus.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )

            else -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
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

        BackButton(onBackClicked)

        if (uiState.saveButtonVisible) {
            SaveButton(onSaveClicked, uiState.isSaved)
        }
    }
}

@Composable
private fun PlantDetails(
    plant: PlantDetailUiData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        AsyncImage(
            model = plant.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = plant.commonName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        plant.latinName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(14.dp))

        PlantTraitsRow(plant)

        plant.description?.let {
            Spacer(Modifier.height(14.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.height(14.dp))

        plant.matureSize?.let {
            InfoRow(label = stringResource(R.string.mature_size), value = it)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        plant.careLevel?.let {
            InfoRow(label = stringResource(R.string.care_level), value = it)
        }

        plant.toxicityNotice?.let { toxicityNotice ->
            val messageRes = when(toxicityNotice) {
                PlantDetailUiData.ToxicityNotice.PetsAndHumans -> R.string.toxicity_notice_pets_and_humans
                PlantDetailUiData.ToxicityNotice.Pets -> R.string.toxicity_notice_pets
                PlantDetailUiData.ToxicityNotice.Humans -> R.string.toxicity_notice_humans
            }
            Spacer(Modifier.height(14.dp))
            CautionNotice(text = stringResource(messageRes))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PlantTraitsRow(plant: PlantDetailUiData) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        plant.lightLabel?.let {
            TraitCard(
                eyebrow = stringResource(R.string.trait_light),
                value = it,
                iconRes = R.drawable.ic_light_sun,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
        plant.waterLabel?.let {
            TraitCard(
                eyebrow = stringResource(R.string.trait_water),
                value = it,
                iconRes = R.drawable.ic_water_drop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
        plant.toxicToPets?.let { toxic ->
            TraitCard(
                eyebrow = stringResource(R.string.trait_pets),
                value = stringResource(if (toxic) R.string.trait_toxic else R.string.trait_safe),
                iconRes = R.drawable.ic_toxicity_alert,
                caution = toxic,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun BoxScope.BackButton(onBackClicked: () -> Unit) {
    IconButton(
        onClick = onBackClicked,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp)
            .size(44.dp)
            .align(Alignment.TopStart),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_back),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
private fun BoxScope.SaveButton(
    onSaveClicked: () -> Unit,
    isSaved: Boolean,
) {
    IconButton(
        onClick = onSaveClicked,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        ),
        modifier = Modifier
            .padding(end = 16.dp, top = 8.dp)
            .align(Alignment.TopEnd)
            .size(44.dp)
            .semantics {
                selected = isSaved
            }
    ) {
        Icon(
            painter = painterResource(
                if (isSaved) {
                    R.drawable.ic_saved_bookmark_filled
                } else {
                    R.drawable.ic_saved_bookmark
                }
            ),
            contentDescription = stringResource(R.string.save),
            tint = if (isSaved) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PlantDetailContentPreview() {
    LeafletTheme {
        PlantDetailContent(
            uiState = PlantDetailUiState(
                plant = PlantDetailUiData(
                    commonName = "Monstera deliciosa",
                    latinName = "Monstera deliciosa",
                    imageUrl = null,
                    description = "Climbing evergreen from southern Mexico. " +
                        "Splits its leaves as it matures, so give it a moss pole " +
                        "and room to spread.",
                    lightLabel = "Bright indirect",
                    waterLabel = "Every 7 days",
                    matureSize = "2–3 m",
                    careLevel = "Easy",
                    toxicToPets = true,
                    toxicityNotice = PlantDetailUiData.ToxicityNotice.PetsAndHumans
                ),
            ),
            onBackClicked = {},
            onSaveClicked = {},
            onRetryClicked = {},
        )
    }
}
