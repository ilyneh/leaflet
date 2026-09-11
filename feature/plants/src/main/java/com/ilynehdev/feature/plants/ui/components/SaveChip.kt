package com.ilynehdev.feature.plants.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.ilynehdev.feature.plants.ui.components.SavedFilterChipDefaults.unselectedColor
import com.ilynehdev.feature.plants.ui.components.SavedFilterChipDefaults.selectedColor


@Immutable
internal data class SavedFilterChipColors(
    val pillColor: Color,
    val textColor: Color,
)

internal object SavedFilterChipDefaults {
    @Composable
    fun selectedColor() = SavedFilterChipColors(
        pillColor = MaterialTheme.colorScheme.secondary,
        textColor = MaterialTheme.colorScheme.onSecondary
    )

    @Composable
    fun unselectedColor() = SavedFilterChipColors(
        pillColor = MaterialTheme.colorScheme.tertiaryContainer,
        textColor = MaterialTheme.colorScheme.onTertiaryContainer
    )
}

@Composable
fun SavedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = if (selected) selectedColor() else unselectedColor()
    val iconRes = if (selected) {
        R.drawable.ic_saved_bookmark_filled
    } else {
        R.drawable.ic_saved_bookmark
    }

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
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
