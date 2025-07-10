@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.eur
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.amount.MoneyInputData
import kotlin.test.Test

class AmountInputPreviewTest {

    @Test
    fun displaysInitialAmountText() = runComposeUiTest {
        setContent {
            AppTheme {
                AmountInputPreview(
                    amount = MoneyInputData(
                        money = 0.eur,
                        text = "0.00 €",
                        isValid = false,
                        isHint = true,
                    ),
                    amountMin = StringParam.Disable,
                    amountMax = StringParam.Disable,
                )
            }
        }

        onNodeWithContentDescription("Max allowed amount").assertDoesNotExist()
        onNodeWithContentDescription("Current amount").assertTextEquals("0.00 €")
        onNodeWithContentDescription("Min allowed amount").assertDoesNotExist()
    }

    @Test
    fun displaysHintAmountText() = runComposeUiTest {
        setContent {
            AppTheme {
                AmountInputPreview(
                    amount = MoneyInputData(
                        money = 0.eur,
                        text = "Enter amount",
                        isValid = false,
                        isHint = true,
                    ),
                )
            }
        }

        onNodeWithContentDescription("Current amount").assertTextEquals("Enter amount")
    }

    @Test
    fun displaysAllElementsWithMinAndMaxEnabled() = runComposeUiTest {
        setContent {
            AppTheme {
                AmountInputPreview(
                    amount = MoneyInputData(
                        money = 50.eur,
                        text = "50.00 €",
                        isValid = true,
                        isHint = false,
                    ),
                    amountMin = StringParam.Enable("10.00 €"),
                    amountMax = StringParam.Enable("100.00 €"),
                )
            }
        }

        onNodeWithContentDescription("Max allowed amount").assertTextEquals("max. 100.00 €")
        onNodeWithContentDescription("Current amount").assertTextEquals("50.00 €")
        onNodeWithContentDescription("Min allowed amount").assertTextEquals("min. 10.00 €")
    }
}
