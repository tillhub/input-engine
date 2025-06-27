@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.helper.NumpadKey
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NumberKeyboardTest {

    @Test
    fun numberKeyboard_clicksEmitCorrectNumpadKeys() = runComposeUiTest {
        val receivedKeys = mutableListOf<NumpadKey>()

        setContent {
            NumberKeyboard(
                onClick = { receivedKeys.add(it) },
                showDecimalSeparator = true,
                showNegative = true,
            )
        }

        val digits = listOf(7, 8, 9, 4, 5, 6, 1, 2, 3)

        digits.forEach { digit ->
            onNodeWithContentDescription("Number $digit")
                .assertIsDisplayed()
                .performClick()
        }

        onNodeWithContentDescription("Decimal separator and Negative sign")
            .assertIsDisplayed()
            .performClick()

        onNodeWithContentDescription("Number 0")
            .assertIsDisplayed()
            .performClick()

        onNodeWithContentDescription("Delete")
            .assertIsDisplayed()
            .performClick()

        val expectedKeys = digits.map { NumpadKey.SingleDigit(Digit.from(it)) } +
            listOf(
                NumpadKey.DecimalSeparator,
                NumpadKey.SingleDigit(Digit.ZERO),
                NumpadKey.Delete,
            )

        assertEquals(expectedKeys, receivedKeys)
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

    @Test
    fun numberKeyboard_actionButtons_renderCorrectlyAndClickable() = runComposeUiTest {
        val actions = mutableListOf<NumpadKey>()

        setContent {
            NumberKeyboard(
                onClick = { actions.add(it) },
                showNegative = true,
            )
        }

        onNodeWithContentDescription("Negative sign")
            .assertIsDisplayed()
            .performClick()

        onNodeWithContentDescription("Delete")
            .assertIsDisplayed()
            .performClick()

        assertEquals(listOf(NumpadKey.Negate, NumpadKey.Delete), actions)
    }
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
