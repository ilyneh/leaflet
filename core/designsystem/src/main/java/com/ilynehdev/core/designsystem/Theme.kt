package com.ilynehdev.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary              = Pink40,
    onPrimary            = White,
    primaryContainer     = Pink90,
    onPrimaryContainer   = Pink10,
    inversePrimary       = Pink80,
    secondary            = Green40,
    onSecondary          = White,
    secondaryContainer   = Green60,
    onSecondaryContainer = Green20,
    tertiary             = Cyan40,
    tertiaryContainer    = Cyan90,
    error                = Red40,
    errorContainer       = Red90,
    surface              = Grey99,
    onSurface            = Grey10,
    surfaceVariant       = GreenGrey90,
    onSurfaceVariant     = GreenGrey30,
    outline              = GreenGrey50,
    outlineVariant       = GreenGrey80,

    surfaceContainerHigh = Grey92,
    surfaceContainerHighest = Grey90,
)

private val DarkColors = darkColorScheme(
    primary              = Pink80,
    onPrimary            = Pink20,
    primaryContainer     = Pink30,
    onPrimaryContainer   = Pink90,
    inversePrimary       = Pink40,
    secondary            = Green60,
    onSecondary          = Green10,
    secondaryContainer   = Green30,
    onSecondaryContainer = Green90,
    tertiary             = Cyan80,
    tertiaryContainer    = Cyan30,
    error                = Red80,
    errorContainer       = Red30,
    surface              = Grey5,
    onSurface            = Grey90,
    surfaceVariant       = GreenGrey30,
    onSurfaceVariant     = GreenGrey80,
    outline              = GreenGrey60,
    outlineVariant       = GreenGrey30,
)

@Composable
fun LeafletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}