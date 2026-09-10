package com.ilynehdev.feature.plants.detail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilynehdev.core.designsystem.LeafletTheme
import com.ilynehdev.core.designsystem.PlantCautionColors
import com.ilynehdev.feature.plants.R

@Immutable
internal data class TraitCardColors(
    val background: Color,
    val border: Color,
    val eyebrow: Color,
    val text: Color,
    val icon: Color,
)

internal object TraitCardDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border: Color = MaterialTheme.colorScheme.outlineVariant,
        eyebrow: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        text: Color = MaterialTheme.colorScheme.onSurface,
        icon: Color = MaterialTheme.colorScheme.primary,
    ) = TraitCardColors(background, border, eyebrow, text, icon)

    @Composable
    fun cautionColors() = TraitCardColors(
        background = PlantCautionColors.container,
        border = PlantCautionColors.border,
        eyebrow = PlantCautionColors.eyebrow,
        text = PlantCautionColors.ink,
        icon = PlantCautionColors.ink,
    )
}

@Composable
fun TraitCard(
    eyebrow: String,
    value: String,
    @DrawableRes iconRes:  Int,
    modifier: Modifier = Modifier,
    caution: Boolean = false,
) {
    val colors = if (caution) TraitCardDefaults.cautionColors() else TraitCardDefaults.colors()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.background)
            .border(
                width = 1.dp,
                color = colors.border,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colors.icon,
        )
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.eyebrow,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.text,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TraitCardPreview() {
    LeafletTheme {
        TraitCard(
            eyebrow = stringResource(R.string.trait_light),
            value = "Bright Indirect",
            iconRes = R.drawable.ic_light_sun
        )
    }
}