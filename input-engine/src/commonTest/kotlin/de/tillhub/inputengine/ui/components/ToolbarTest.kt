package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ToolbarTest {

    @Test
    fun testToolbar() = runComposeUiTest {
        var clicked = false
        setContent {
            Toolbar(
                title = "Toolbar title",
                onClick = { clicked = true }
            )
        }

        onNodeWithTag("toolbarTitle").assertTextEquals("Toolbar title")
        onNodeWithTag("toolbarIcon").performClick()
        assertTrue(clicked)
    }
}
