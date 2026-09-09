package com.ilynehdev.feature.plants.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ilynehdev.feature.plants.R

@Composable
fun SearchFilterBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(56.dp),
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
}
