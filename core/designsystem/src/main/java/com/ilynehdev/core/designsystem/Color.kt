package com.ilynehdev.core.designsystem

import androidx.compose.ui.graphics.Color


// Primary — Green
val Green10 = Color(0xFF06280F)
val Green20 = Color(0xFF0B2A16)
val Green30 = Color(0xFF2C5535)
val Green35 = Color(0xFF2C6A55)
val Green40 = Color(0xFF3A6F43)
val Green60 = Color(0xFF59AC77)
val Green90 = Color(0xFFC7E8CE)

// Secondary — Pink
val Pink10 = Color(0xFF40071A)
val Pink20 = Color(0xFF5C1229)
val Pink30 = Color(0xFF8A3149)
val Pink40 = Color(0xFFB3244C)
val Pink50 = Color(0xFFC25774)
val Pink80 = Color(0xFFF3B6C4)
val Pink90 = Color(0xFFFFDCE4)

// Tertiary — Cyan
val Cyan30 = Color(0xFF204D58)
val Cyan40 = Color(0xFF3A6470)
val Cyan80 = Color(0xFFA2CEDC)
val Cyan90 = Color(0xFFBEEAF8)

// Error — Red
val Red30 = Color(0xFF93000A)
val Red40 = Color(0xFFBA1A1A)
val Red80 = Color(0xFFFFB4AB)
val Red90 = Color(0xFFFFDAD6)

// Neutral — Grey
val Grey5  = Color(0xFF0F1512)
val Grey10 = Color(0xFF191C1A)
val Grey20 = Color(0xFF2B322E)
val Grey90 = Color(0xFFDBE5DE)
val Grey92 = Color(0xFFE6EDE9)
val Grey94 = Color(0xFFEBF2ED)
val Grey96 = Color(0xFFEFF6F2)
val Grey98 = Color(0xFFF4FBF6)

// Neutral variant — GreenGrey
val GreenGrey10 = Color(0xFF04231A)
val GreenGrey30 = Color(0xFF404944)
val GreenGrey40 = Color(0xFF3E5D51)
val GreenGrey50 = Color(0xFF5E8377)
val GreenGrey60 = Color(0xFF8A938D)
val GreenGrey70 = Color(0xFF8FAAA0)
val GreenGrey80 = Color(0xFFC0C9C2)
val GreenGrey90 = Color(0xFFDCEDE3)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)


// Caution colors - content advisory.
// Not used in ColorScheme. Reached through PlantCautionColors.
val Caution30 = Color(0xFF8E3040)
val Caution40 = Color(0xFFB3244C)
val Caution80 = Color(0xFFFFD5D5)
val Caution90 = Color(0xFFFFF3F5)

/**
 * Caution lives outside the ColorScheme on purpose. It is an advisory about
 * the plant, not a state of the UI, so nothing should be able to pick it up
 * by touching a standard M3 role.
 */
object PlantCautionColors {
    val ink = Caution40
    val eyebrow = Caution30
    val border = Caution80
    val container = Caution90
}
