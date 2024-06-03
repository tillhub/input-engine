package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class ToolbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    interface ButtonEvents {
        fun onClick()
    }

    @Test
    fun testToolbar() {
        val events: ButtonEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                Toolbar(
                    title = "Toolbar title",
                    onClick = events::onClick
                )
            }

            onNodeWithTag("toolbarTitle").assertTextEquals("Toolbar title")
            onNodeWithTag("toolbarIcon").performClick()

            verify {
                events.onClick()
            }
        }
    }
}