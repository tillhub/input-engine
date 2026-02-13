package de.tillhub.inputengine.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Determines whether the application is currently in night (dark) mode.
 *
 * This Android-specific implementation checks the [AppCompatDelegate] for a manually
 * set night mode preference. If no specific preference is set (i.e., [AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM]
 * or [AppCompatDelegate.MODE_NIGHT_UNSPECIFIED]), it falls back to the system-wide
 * dark theme setting.
 *
 * @return `true` if night mode is active, `false` otherwise.
 */
@Composable
internal actual fun isNightMode(): Boolean = when (AppCompatDelegate.getDefaultNightMode()) {
    AppCompatDelegate.MODE_NIGHT_NO -> false
    AppCompatDelegate.MODE_NIGHT_YES -> true
    else -> isSystemInDarkTheme()
}