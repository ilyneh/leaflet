package com.ilynehdev.feature.plants.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.R

@Composable
fun PlantDetailScreen(
    plantId: Long,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlantDetailContent(
        plantId = plantId,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun PlantDetailContent(
    plantId: Long,
    onBackClicked: () -> Unit,
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

        Text(
            text = "Plant $plantId",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantDetailContentPreview() {
    LeafletTheme {
        PlantDetailContent(
            plantId = 16L,
            onBackClicked = {},
        )
    }
}
