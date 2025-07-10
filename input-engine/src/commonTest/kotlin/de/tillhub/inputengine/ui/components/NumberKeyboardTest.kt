@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.testing.runCustomComposeUiTest
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberKeyboardTest {

    @Test
    fun testDefaultState() = runComposeUiTest {
        setContent {
            AppTheme {
                NumberKeyboard(
                    decimalSeparator = ".",
                ) { }
            }
        }

        onNodeWithContentDescription("Number button 0").assertTextEquals("0")
        onNodeWithContentDescription("Number button 1").assertTextEquals("1")
        onNodeWithContentDescription("Number button 2").assertTextEquals("2")
        onNodeWithContentDescription("Number button 3").assertTextEquals("3")
        onNodeWithContentDescription("Number button 4").assertTextEquals("4")
        onNodeWithContentDescription("Number button 5").assertTextEquals("5")
        onNodeWithContentDescription("Number button 6").assertTextEquals("6")
        onNodeWithContentDescription("Number button 7").assertTextEquals("7")
        onNodeWithContentDescription("Number button 8").assertTextEquals("8")
        onNodeWithContentDescription("Number button 9").assertTextEquals("9")
        onNodeWithContentDescription("Clear").assertTextEquals("C")
        onNodeWithContentDescription("Delete").assertTextEquals("←")
    }

    @Test
    fun testShowNegative() = runComposeUiTest {
        setContent {
            AppTheme {
                NumberKeyboard(
                    showNegative = true,
                    decimalSeparator = ".",
                ) { }
            }
        }

        onNodeWithContentDescription("Negative sign").assertTextEquals("-")
        onNodeWithText("C").assertDoesNotExist()
    }

    @Test
    fun testDecimalSeparator() = runComposeUiTest {
        setContent {
            AppTheme {
                NumberKeyboard(
                    showDecimalSeparator = true,
                    decimalSeparator = ".",
                ) { }
            }
        }

        onNodeWithContentDescription("Decimal separator").assertTextEquals(".")
        onNodeWithText("C").assertDoesNotExist()
        onNodeWithText("-").assertDoesNotExist()
    }

    @Test
    fun testShowDecimalAndNegative() = runComposeUiTest {
        setContent {
            AppTheme {
                NumberKeyboard(
                    showNegative = true,
                    showDecimalSeparator = true,
                    decimalSeparator = ".",
                ) { }
            }
        }

        onNodeWithContentDescription("Decimal separator and Negative sign").assertTextEquals(".\n-")
        onNodeWithText("C").assertDoesNotExist()
    }

    @Test
    fun testNumpadKey() = runCustomComposeUiTest(
        size = Size(1024.0f, 1024.0f),
    ) {
        val events = mutableListOf<NumpadKey>()

        setContent {
            AppTheme {
                NumberKeyboard(
                    decimalSeparator = ".",
                ) { events.add(it) }
            }
        }

        onNodeWithContentDescription("Number button 7").performClick()
        onNodeWithContentDescription("Number button 8").performClick()
        onNodeWithContentDescription("Number button 9").performClick()
        onNodeWithContentDescription("Number button 4").performClick()
        onNodeWithContentDescription("Number button 5").performClick()
        onNodeWithContentDescription("Number button 6").performClick()
        onNodeWithContentDescription("Number button 0").performClick()
        onNodeWithContentDescription("Number button 2").performClick()

        assertEquals(8, events.size, "onClick should be called once")

        assertEquals(
            listOf<NumpadKey>(
                NumpadKey.SingleDigit(Digit.SEVEN),
                NumpadKey.SingleDigit(Digit.EIGHT),
                NumpadKey.SingleDigit(Digit.NINE),
                NumpadKey.SingleDigit(Digit.FOUR),
                NumpadKey.SingleDigit(Digit.FIVE),
                NumpadKey.SingleDigit(Digit.SIX),
                NumpadKey.SingleDigit(Digit.ZERO),
                NumpadKey.SingleDigit(Digit.TWO),
//                NumpadKey.SingleDigit(Digit.EIGHT),
//                NumpadKey.SingleDigit(Digit.NINE),
            ),
            events.toList(),
            "onClick should be called with correct NumpadKey",
        )
    }
}
