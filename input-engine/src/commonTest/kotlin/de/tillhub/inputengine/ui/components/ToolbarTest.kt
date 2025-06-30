package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ToolbarTest {

    @Test
    fun testToolbar() = runComposeUiTest {
        val onClickMock = mock<() -> Unit>(mode = MockMode.autofill)

        setContent {
            Toolbar(
                title = "Toolbar title",
                onClick = onClickMock,
            )
        }

        onNodeWithContentDescription("toolbarTitle").assertTextEquals("Toolbar title")
        onNodeWithContentDescription("toolbarIcon").performClick()

        verify { onClickMock() }
    }
}
