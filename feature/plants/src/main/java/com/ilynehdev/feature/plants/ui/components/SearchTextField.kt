package com.ilynehdev.feature.plants.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.R

@Composable
fun SearchTextField(
    value: String,
    placeHolderText: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeHolderText) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_discover),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(size = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedTextColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
        ),
    )
}

@Preview
@Composable
fun SearchTextFieldPreview() {
    LeafletTheme {
        SearchTextField(
            value = "Skirll",
            placeHolderText = "Search friends...",
            onValueChange = {},
        )

        SearchTextField(
            value = "Skirll",
            placeHolderText = "Search friends...",
            onValueChange = {},
        )
    }
}
