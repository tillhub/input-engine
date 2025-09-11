@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class PinInputPreviewTest {
    @Test
    fun displaysInitialPinText() = runComposeUiTest {
        setContent {
            AppTheme {
                PinInputPreview(
                    pinText = "1234",
                    hintText = "****",
                    overridePinInput = false,
                    onOverride = {},
                )
            }
        }

        // Verify that the text is masked due to PasswordVisualTransformation
        // The actual text "1234" should be displayed as "••••" (bullet characters)
        onNodeWithTag("Pin placeholder").assertExists()
        onNodeWithTag("Pin placeholder").assertTextEquals("••••")
        onNodeWithContentDescription("Override pin").assertDoesNotExist()
    }

    @Test
    fun displaysPlaceholderWhenPinTextIsEmpty() = runComposeUiTest {
        setContent {
            AppTheme {
                PinInputPreview(
                    pinText = "",
                    hintText = "****",
                    overridePinInput = false,
                    onOverride = {},
                )
            }
        }

        // When pinText is empty, just verify the component exists
        onNodeWithTag("Pin placeholder").assertExists()
        onNodeWithContentDescription("Override pin").assertDoesNotExist()
    }

    @Test
    fun displaysMaskedTextForDifferentLengths() = runComposeUiTest {
        setContent {
            AppTheme {
                PinInputPreview(
                    pinText = "12",
                    hintText = "****",
                    overridePinInput = false,
                    onOverride = {},
                )
            }
        }

        // Verify that 2-character input is masked as "••"
        onNodeWithTag("Pin placeholder").assertExists()
        onNodeWithTag("Pin placeholder").assertTextEquals("••")
    }

    @Test
    fun onOverridePinInputTest() = runComposeUiTest {
        var overrideCount = 0

        setContent {
            AppTheme {
                PinInputPreview(
                    pinText = "",
                    hintText = "****",
                    overridePinInput = true,
                    onOverride = {
                        overrideCount++
                    },
                )
            }
        }

        // Check if override button is visible and clickable
        onNodeWithTag("Pin placeholder").assertExists()
        onNodeWithContentDescription("Override pin").assertExists()
        onNodeWithContentDescription("Override pin").performClick()

        assertEquals(1, overrideCount, "onOverride should be called once")
    }
}
