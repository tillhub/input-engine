package de.tillhub.inputengine.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun AppTheme(
    useDarkTheme: Boolean = isNightMode(),
    content: @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        lightColorScheme
    } else {
        darkColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography(),
        shapes = shape,
        content = content,
    )
}

@Composable
internal expect fun isNightMode(): Boolean