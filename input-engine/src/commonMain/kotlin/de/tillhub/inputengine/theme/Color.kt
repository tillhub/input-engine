package de.tillhub.inputengine.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val OrbitalBlue = Color(0xFF279FF6)
val GalacticBlue = Color(0xFF001D34)
val GalacticVariant = Color(0xFF253950)
val LightGray = Color(0xFFECF1F4)
val TextGray = Color(0xFF757575)
val MagneticGrey = Color(0xFFA7ABB1)
val ContrailWhite = Color(0xFFE9F5FE)

val ColorScheme.hintColor: Color
    get() = MagneticGrey

val lightColorScheme = lightColorScheme(
    background = Color.White,
    primary = OrbitalBlue,
    onPrimary = Color.White,
    primaryContainer = OrbitalBlue,
    secondaryContainer = LightGray,
    onSecondaryContainer = GalacticBlue,
    onSecondary = TextGray,
    onBackground = GalacticBlue,
)

val darkColorScheme = darkColorScheme(
    background = GalacticBlue,
    primary = OrbitalBlue,
    onPrimary = Color.White,
    primaryContainer = OrbitalBlue,
    secondaryContainer = GalacticVariant,
    onSecondaryContainer = ContrailWhite,
    onSecondary = ContrailWhite,
    onBackground = Color.White,
)
