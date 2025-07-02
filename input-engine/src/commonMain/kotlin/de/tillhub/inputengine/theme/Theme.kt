package de.tillhub.inputengine.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}
