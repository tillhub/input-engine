package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.helper.NumpadKey
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NumberButtonTest {

    @Test
    fun testNumberButtonClick_sendsCorrectDigit() = runComposeUiTest {
        var clickedKey: NumpadKey? = null

        setContent {
            NumberButton(
                number = Digit.from(7),
                onClick = { clickedKey = it },
            )
        }

        onNodeWithText("7").assertIsDisplayed()
        onNodeWithContentDescription("Number 7").performClick()

        assertEquals(NumpadKey.SingleDigit(Digit.from(7)), clickedKey)
    }
}
