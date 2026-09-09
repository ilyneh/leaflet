package com.ilynehdev.feature.plants.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun PulsingIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    from: Color = MaterialTheme.colorScheme.secondary,
    to: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = pulsingColor(from, to),
        modifier = modifier,
    )
}

@Composable
fun pulsingColor(
    from: Color = MaterialTheme.colorScheme.secondary,
    to: Color = MaterialTheme.colorScheme.secondaryContainer,
): Color {
    val transition = rememberInfiniteTransition(label = "pulse")
    val color by transition.animateColor(
        initialValue = from,
        targetValue = to,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseColor"
    )
    return color
}