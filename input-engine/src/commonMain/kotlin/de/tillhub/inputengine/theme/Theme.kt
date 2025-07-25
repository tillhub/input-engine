package de.tillhub.inputengine.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
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
