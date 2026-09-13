package com.ilynehdev.feature.plants.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class FilterOptionChipColors(
    val pillColor: Color,
    val borderColor: Color,
    val textColor: Color,
)

object FilterOptionChipDefaults {
    @Composable
    fun colors(
        pillColor: Color = Color.Transparent,
        borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
        textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    ) = FilterOptionChipColors(pillColor, borderColor, textColor)

    @Composable
    fun selectedColors(
        pillColor: Color = MaterialTheme.colorScheme.secondary,
        borderColor: Color = Color.Transparent,
        textColor: Color = MaterialTheme.colorScheme.onSecondary,
    ) = FilterOptionChipColors(pillColor, borderColor, textColor)
}

@Composable
fun FilterOptionChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = if (selected) {
        FilterOptionChipDefaults.selectedColors()
    } else {
        FilterOptionChipDefaults.colors()
    }

    val shape = RoundedCornerShape(percent = 50)
    Text(
        text = label,
        color = colors.textColor,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier
            .clip(shape)
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onToggle() },
            )
            .background(color = colors.pillColor, shape = shape)
            .border(width = 1.dp, color = colors.borderColor, shape = shape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}
