package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.helper.NumpadKey
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class NumberButtonTest {

    @Test
    fun testNumberButtonClick_sendsCorrectDigit() = runComposeUiTest {
        val onClickMock = mock<(NumpadKey) -> Unit>(mode = MockMode.autofill)

        setContent {
            NumberButton(
                number = Digit.from(7),
                onClick = onClickMock,
            )
        }

        onNodeWithText("7").assertIsDisplayed()
        onNodeWithContentDescription("Number 7").performClick()

        verify { onClickMock(NumpadKey.SingleDigit(Digit.from(7))) }
    }
}
