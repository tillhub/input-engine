package de.tillhub.inputengine.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val HintGray = Color(0xFF666666)
val LunarGray = Color(0xFFE9EAEB)
val Tint = Color(0xFFFFFFFF)
val GalacticBlue = Color(0xAA232E3D)
val ExtraButtonTint = Color(0xFFECEFF0)
val OrbitalBlue = Color(0xFF279FF6)
val SoyuzGrey = Color(0xFF6E737A)
val MagneticGrey = Color(0xFFA7ABB1)

val ColorScheme.hintColor: Color
    get() = MagneticGrey

val colors = lightColorScheme(
    primary = OrbitalBlue,
    onPrimary = Tint,
)
