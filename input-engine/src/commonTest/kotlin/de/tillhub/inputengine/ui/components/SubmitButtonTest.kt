package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SubmitButtonTest {

    @Test
    fun testEnabledSubmitButton() = runComposeUiTest {
        val onClickMock = mock<() -> Unit>(mode = MockMode.autofill)

        setContent {
            SubmitButton(
                isEnable = true,
                onClick = onClickMock,
            )
        }

        onNodeWithContentDescription("submitButton").assertIsEnabled()
        onNodeWithText("Submit").assertIsDisplayed()
        onNodeWithContentDescription("submitButton").performClick()

        verify { onClickMock() }
    }

    @Test
    fun testDisabledSubmitButton() = runComposeUiTest {
        val onClickMock = mock<() -> Unit>(mode = MockMode.autofill)

        setContent {
            SubmitButton(
                isEnable = false,
                onClick = onClickMock,
            )
        }

        onNodeWithContentDescription("submitButton").assertIsNotEnabled()
        onNodeWithText("Submit").assertIsDisplayed()
        onNodeWithContentDescription("submitButton").performClick()

        // No click should be registered
        verify(mode = VerifyMode.not) { onClickMock() }
    }
}

