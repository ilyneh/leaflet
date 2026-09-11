package com.ilynehdev.feature.plants.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.feature.plants.R


@Immutable
internal data class SavedFilterChipColors(
    val pillColor: Color,
    val textColor: Color,
)

internal object SavedFilterChipDefaults {
    @Composable
    fun colors(
        pillColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    ) = SavedFilterChipColors(pillColor, textColor)

    @Composable
    fun selectedColors(
        pillColor: Color = MaterialTheme.colorScheme.secondary,
        textColor: Color = MaterialTheme.colorScheme.onSecondary,
    ) = SavedFilterChipColors(pillColor, textColor)
}

@Composable
fun SavedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = if (selected) SavedFilterChipDefaults.selectedColors() else SavedFilterChipDefaults.colors()
    val iconRes = if (selected) {
        R.drawable.ic_saved_bookmark_filled
    } else {
        R.drawable.ic_saved_bookmark
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            )
            .background(colors.pillColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colors.textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.saved),
            color = colors.textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedFilterChipPreview() {
    LeafletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SavedFilterChip(selected = true, onClick = {})
            SavedFilterChip(selected = false, onClick = {})
        }
    }
}
