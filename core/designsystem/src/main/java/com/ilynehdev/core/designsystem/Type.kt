package com.ilynehdev.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = DisplayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = DisplayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = DisplayFontFamily),

    headlineLarge = baseline.headlineLarge.copy(fontFamily = DisplayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = DisplayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = DisplayFontFamily),

    titleLarge = baseline.titleLarge.copy(fontFamily = DisplayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = DisplayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = DisplayFontFamily),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = BodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = BodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = BodyFontFamily),

    labelLarge = baseline.labelLarge.copy(fontFamily = BodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = BodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = BodyFontFamily)
)

@Preview(name = "Small Font", fontScale = 0.85f)
@Preview(name = "Default Font", fontScale = 1.0f)
@Preview(name = "Large Font", fontScale = 1.15f)
@Composable
fun TypographyPreview() {
    LeafletTheme {
        Column(modifier = Modifier.background(GreenGrey90)) {
            Text(
                text = "Checking Typography HeadlineMedium",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Checking Typography DisplayLarge",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Checking Typography TitleLarge",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Checking Typography BodyLarge",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Checking Typography LabelSmall",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}