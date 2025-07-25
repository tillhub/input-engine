@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.QuantityInputData
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityInputPreviewTest {
    @Test
    fun displaysInitialPercentageText() = runComposeUiTest {
        setContent {
            AppTheme {
                QuantityInputPreview(
                    quantity =
                    QuantityInputData(
                        qty = QuantityIO.ZERO,
                        text = "0",
                        isValid = false,
                        isHint = false,
                    ),
                    minQuantity = StringParam.Disable,
                    maxQuantity = StringParam.Disable,
                    decrease = {},
                    increase = {},
                )
            }
        }

        onNodeWithContentDescription("Button decrease").assertIsDisplayed()
        onNodeWithContentDescription("Max allowed quantity").assertDoesNotExist()
        onNodeWithContentDescription("Current quantity").assertTextEquals("0")
        onNodeWithContentDescription("Min allowed quantity").assertDoesNotExist()
        onNodeWithContentDescription("Button increase").assertIsDisplayed()
    }

    @Test
    fun decreaseButtonClicked() = runComposeUiTest {
        var increaseCount = 0
        var decreaseCount = 0

        setContent {
            AppTheme {
                QuantityInputPreview(
                    quantity =
                    QuantityInputData(
                        qty = QuantityIO.ZERO,
                        text = "0",
                        isValid = false,
                        isHint = false,
                    ),
                    minQuantity = StringParam.Enable("0"),
                    maxQuantity = StringParam.Enable("10"),
                    decrease = {
                        decreaseCount++
                    },
                    increase = {
                        increaseCount++
                    },
                )
            }
        }

        onNodeWithContentDescription("Max allowed quantity").assertTextEquals("Max. 10")
        onNodeWithContentDescription("Current quantity").assertTextEquals("0")
        onNodeWithContentDescription("Min allowed quantity").assertTextEquals("Min. 0")

        onNodeWithContentDescription("Button decrease").performClick()

        assertEquals(0, increaseCount, "increase should not be called")
        assertEquals(1, decreaseCount, "decrease should be called once")
    }

    @Test
    fun increaseButtonClicked() = runComposeUiTest {
        var increaseCount = 0
        var decreaseCount = 0

        setContent {
            AppTheme {
                QuantityInputPreview(
                    quantity =
                    QuantityInputData(
                        qty = QuantityIO.ZERO,
                        text = "2",
                        isValid = false,
                        isHint = false,
                    ),
                    minQuantity = StringParam.Enable("0"),
                    maxQuantity = StringParam.Enable("10"),
                    decrease = {
                        decreaseCount++
                    },
                    increase = {
                        increaseCount++
                    },
                )
            }
        }

        onNodeWithContentDescription("Max allowed quantity").assertTextEquals("Max. 10")
        onNodeWithContentDescription("Current quantity").assertTextEquals("2")
        onNodeWithContentDescription("Min allowed quantity").assertTextEquals("Min. 0")

        onNodeWithContentDescription("Button increase").performClick()

        assertEquals(1, increaseCount, "increase should be called once")
        assertEquals(0, decreaseCount, "decrease should not be called")
    }
}
