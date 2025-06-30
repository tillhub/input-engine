@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.helper.NumpadKey
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberKeyboardTest {

    internal interface NumpadEvents {
        fun onClick(key: NumpadKey)
    }

    @Test
    fun testDefaultStateAndEvents() = runComposeUiTest {
        val events: NumpadEvents = mock(mode = MockMode.autofill)

        setContent {
            NumberKeyboard(
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

    @Test
    fun testShowMinusAction() = runComposeUiTest {
        val events: NumpadEvents = mock(mode = MockMode.autofill)

        setContent {
            NumberKeyboard(
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

    @Test
    fun testShowDecimal() = runComposeUiTest {
        val events: NumpadEvents = mock(mode = MockMode.autofill)

        setContent {
            NumberKeyboard(
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

    @Test
    fun numberKeyboard_buttonTraversalOrder_matchesExpected() = runComposeUiTest {
        setContent {
            NumberKeyboard(
                onClick = {},
                showDecimalSeparator = true,
                showNegative = true,
            )
        }

        val expectedDigitOrder = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3, 0)

        val actualOrder = onAllNodes(hasContentDescriptionPrefix("Number "))
            .fetchSemanticsNodes()
            .mapNotNull {
                it.config[SemanticsProperties.ContentDescription]
                    .firstOrNull()
                    ?.removePrefix("Number ")
                    ?.toIntOrNull()
            }

        assertEquals(expectedDigitOrder, actualOrder)
    }

    fun hasContentDescriptionPrefix(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("ContentDescription starts with \"$prefix\"") { node ->
            if (SemanticsProperties.ContentDescription in node.config) {
                val descList = node.config[SemanticsProperties.ContentDescription]
                descList.any { it.startsWith(prefix) } == true
            } else {
                false
            }
        }
    }
}
