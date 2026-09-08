package com.ilynehdev.feature.plants.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ilynehdev.core.designsystem.LeafletTheme


@Composable
fun MainHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainHeaderPreview() {
    LeafletTheme {
        MainHeader("Plants")
    }
}
