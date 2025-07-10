@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberButtonTest {

    @Test
    fun initialButtonState() = runComposeUiTest {
        var onClickCount = 0

        setContent {
            AppTheme {
                NumberButton(
                    modifier = Modifier.testTag("numberButton"),
                    number = Digit.ONE,
                ) {
                    onClickCount++
                }
            }
        }

        onNodeWithText("1").assertIsDisplayed()

        onNodeWithTag("numberButton").performClick()

        assertEquals(1, onClickCount, "onClick should be called once")
    }
}
