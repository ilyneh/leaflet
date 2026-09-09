package com.ilynehdev.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Green40,
    onPrimary = White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    inversePrimary = Green60,

    secondary = Pink50,
    onSecondary = White,
    secondaryContainer = Pink90,
    onSecondaryContainer = Pink10,

    tertiary = Green35,
    onTertiary = White,
    tertiaryContainer = GreenGrey90,
    onTertiaryContainer = Green35,

    error = Red40,
    onError = White,
    errorContainer = Red90,
    onErrorContainer = Red30,

    background = Grey98,
    onBackground = GreenGrey10,

    surface = Grey98,
    onSurface = GreenGrey10,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey40,

    surfaceContainerLowest = White,
    surfaceContainerLow = Grey96,
    surfaceContainer = Grey94,
    surfaceContainerHigh = Grey92,
    surfaceContainerHighest = Grey90,

    surfaceBright = Grey98,
    surfaceDim = Grey90,
    surfaceTint = Green40,

    outline = GreenGrey70,
    outlineVariant = GreenGrey90,

    inverseSurface = Grey20,
    inverseOnSurface = Grey96,
    scrim = Black,
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