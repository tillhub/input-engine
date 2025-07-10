@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class SubmitButtonTest {

    @Test
    fun submitButtonTest() = runComposeUiTest {
        var clicks = 0

        setContent {
            AppTheme {
                SubmitButton(
                    isEnable = true,
                    modifier = Modifier.testTag("submitButton"),
                    onClick = {
                        clicks++
                    },
                )
            }
        }

        onNodeWithTag("submitButton").performClick()
        onNodeWithContentDescription("Submit button label").assertTextEquals("Submit")

        assertEquals(1, clicks, "onClick should be called once")
    }
}
