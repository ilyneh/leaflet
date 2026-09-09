package com.ilynehdev.core.designsystem

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight


internal val DisplayFontFamily = FontFamily(
    Font(R.font.instrumentserif_regular, FontWeight.Normal),
    Font(R.font.instrumentserif_italic, FontWeight.Normal, FontStyle.Italic),
)

internal val BodyFontFamily = FontFamily(
    // Regular Style
    dmSans(FontWeight.Light, FontStyle.Normal),
    dmSans(FontWeight.Normal, FontStyle.Normal),
    dmSans(FontWeight.Medium, FontStyle.Normal),
    dmSans(FontWeight.SemiBold, FontStyle.Normal),
    dmSans(FontWeight.Bold, FontStyle.Normal),
    dmSans(FontWeight.ExtraBold, FontStyle.Normal),

    // Italic Style
    dmSans(FontWeight.Light, FontStyle.Italic),
    dmSans(FontWeight.Normal, FontStyle.Italic),
    dmSans(FontWeight.Medium, FontStyle.Italic),
    dmSans(FontWeight.SemiBold, FontStyle.Italic),
    dmSans(FontWeight.Bold, FontStyle.Italic),
    dmSans(FontWeight.ExtraBold, FontStyle.Italic),
)

private fun dmSans(weight: FontWeight, style: FontStyle): Font {
    val resId = if (style == FontStyle.Italic) {
        R.font.dm_sans_italic_variable
    } else {
        R.font.dm_sans_variable
    }
    return Font(
        resId = resId,
        weight = weight,
        style = style,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
    )
}
