package de.tillhub.inputengine.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun isNightMode(): Boolean = isSystemInDarkTheme()
