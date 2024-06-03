package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class NumpadKeyboardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    internal interface NumpadEvents {
        fun onClick(key: NumpadKey)
    }

    @Test
    fun testDefaultStateAndEvents() {
        val events: NumpadEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                Numpad(
                    onClick = events::onClick
                )
            }
            onNodeWithText("1").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("3").assertIsDisplayed()
            onNodeWithText("4").assertIsDisplayed()
            onNodeWithText("5").assertIsDisplayed()
            onNodeWithText("6").assertIsDisplayed()
            onNodeWithText("7").assertIsDisplayed()
            onNodeWithText("8").assertIsDisplayed()
            onNodeWithText("9").assertIsDisplayed()
            onNodeWithText("0").assertIsDisplayed()
            onNodeWithText("C").assertIsDisplayed()
            onNodeWithText("←").assertIsDisplayed()
            onNodeWithText(".").assertIsNotDisplayed()
            onNodeWithText("-").assertIsNotDisplayed()

            onNodeWithText("1").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.ONE))
            }

            onNodeWithText("2").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.TWO))
            }

            onNodeWithText("3").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.THREE))
            }

            onNodeWithText("4").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.FOUR))
            }

            onNodeWithText("5").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.FIVE))
            }

            onNodeWithText("6").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.SIX))
            }

            onNodeWithText("7").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.SEVEN))
            }

            onNodeWithText("8").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.EIGHT))
            }

            onNodeWithText("9").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.NINE))
            }

            onNodeWithText("0").performClick()
            verify {
                events.onClick(NumpadKey.SingleDigit(Digit.ZERO))
            }

            onNodeWithText("C").performClick()
            verify {
                events.onClick(NumpadKey.Clear)
            }

            onNodeWithText("←").performClick()
            verify {
                events.onClick(NumpadKey.Delete)
            }
        }
    }

    @Test
    fun testShowMinusAction() {
        val events: NumpadEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                Numpad(
                    showNegative = true,
                    onClick = events::onClick
                )
            }

            onNodeWithText("1").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("3").assertIsDisplayed()
            onNodeWithText("4").assertIsDisplayed()
            onNodeWithText("5").assertIsDisplayed()
            onNodeWithText("6").assertIsDisplayed()
            onNodeWithText("7").assertIsDisplayed()
            onNodeWithText("8").assertIsDisplayed()
            onNodeWithText("9").assertIsDisplayed()
            onNodeWithText("0").assertIsDisplayed()
            onNodeWithText("-").assertIsDisplayed()
            onNodeWithText("←").assertIsDisplayed()

            onNodeWithText(".").assertIsNotDisplayed()
            onNodeWithText("C").assertIsNotDisplayed()

            onNodeWithText("-").performClick()
            verify {
                events.onClick(NumpadKey.Negate)
            }
        }
    }

    @Test
    fun testShowDecimal() {
        val events: NumpadEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                Numpad(
                    showDecimalSeparator = true,
                    onClick = events::onClick
                )
            }

            onNodeWithText("1").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("3").assertIsDisplayed()
            onNodeWithText("4").assertIsDisplayed()
            onNodeWithText("5").assertIsDisplayed()
            onNodeWithText("6").assertIsDisplayed()
            onNodeWithText("7").assertIsDisplayed()
            onNodeWithText("8").assertIsDisplayed()
            onNodeWithText("9").assertIsDisplayed()
            onNodeWithText("0").assertIsDisplayed()
            onNodeWithText(".").assertIsDisplayed()
            onNodeWithText("←").assertIsDisplayed()

            onNodeWithText("-").assertIsNotDisplayed()
            onNodeWithText("C").assertIsNotDisplayed()

            onNodeWithText(".").performClick()
            verify {
                events.onClick(NumpadKey.DecimalSeparator)
            }
        }
    }
}