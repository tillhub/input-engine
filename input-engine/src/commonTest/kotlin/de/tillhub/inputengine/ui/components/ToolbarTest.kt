@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ToolbarTest {

    @Test
    fun toolbarTest() = runComposeUiTest {
        var onBackClickCount = 0

        setContent {
            AppTheme {
                Toolbar(
                    title = "Title",
                    onBackClick = {
                        onBackClickCount++
                    },
                )
            }
        }

        onNodeWithContentDescription("Toolbar title").assertTextEquals("Title")
        onNodeWithContentDescription("Toolbar back button").performClick()

        assertEquals(1, onBackClickCount, "onBackClick should be called once")
    }
}
