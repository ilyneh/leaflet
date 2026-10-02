package com.ilynehdev.feature.plants.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilynehdev.core.designsystem.LeafletTheme


@Composable
internal fun MainHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier.padding(top = 18.dp, bottom = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
internal fun MainHeaderPreview() {
    LeafletTheme {
        MainHeader("Plants")
    }
}
