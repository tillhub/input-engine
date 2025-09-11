@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.eur
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.AmountInputViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

class AmountInputScreenTest {

    private interface TestAmountInputScreen {
        fun onResult(result: AmountInputResult.Success)
        fun onDismiss()
    }

    /**
     * Simple stub implementation of MoneyFormatter for testing.
     * Returns a predictable format: "{currency}{amount}" (e.g., "EUR1000.0" for €10.00)
     */
    private class TestMoneyFormatter : MoneyFormatter {
        override fun format(money: MoneyIO): String = "${money.currency.isoCode}${money.toDouble()}"
    }

    @Test
    fun testOnResult() = runComposeUiTest {
        val test = mock<TestAmountInputScreen> {
            every { onResult(any()) } returns Unit
        }

        setContent {
            AppTheme {
                AmountInputScreen(
                    onResult = test::onResult,
                    onDismiss = test::onDismiss,
                    viewModel = AmountInputViewModel(
                        request = AmountInputRequest(
                            amount = 5.eur,
                            amountMin = MoneyParam.Enable(1.eur),
                            amountMax = MoneyParam.Enable(10.eur),
                            toolbarTitle = StringParam.Enable("Test Amount Input"),
                        ),
                        formatter = TestMoneyFormatter(),
                    ),
                )
            }
        }

        onNodeWithContentDescription("Toolbar title").assertTextEquals("Test Amount Input")

        onNodeWithContentDescription("Max allowed amount").assertTextEquals("Max. EUR1000.0")
        onNodeWithContentDescription("Current amount").assertTextEquals("EUR500.0")
        onNodeWithContentDescription("Min allowed amount").assertTextEquals("Min. EUR100.0")

        onNodeWithContentDescription("Submit button").performClick()

        verify {
            test.onResult(
                AmountInputResult.Success(
                    amount = 5.eur,
                ),
            )
        }
    }

    @Test
    fun testOnDismiss() = runComposeUiTest {
        val test = mock<TestAmountInputScreen> {
            every { onDismiss() } returns Unit
        }

        setContent {
            AppTheme {
                AmountInputScreen(
                    onResult = test::onResult,
                    onDismiss = test::onDismiss,
                    viewModel = AmountInputViewModel(
                        request = AmountInputRequest(
                            amount = 5.eur,
                            amountMin = MoneyParam.Enable(1.eur),
                            amountMax = MoneyParam.Enable(10.eur),
                            toolbarTitle = StringParam.Enable("Test Amount Input"),
                        ),
                        formatter = TestMoneyFormatter(),
                    ),
                )
            }
        }

        onNodeWithContentDescription("Toolbar back button").performClick()

        verify {
            test.onDismiss()
        }
    }
}
