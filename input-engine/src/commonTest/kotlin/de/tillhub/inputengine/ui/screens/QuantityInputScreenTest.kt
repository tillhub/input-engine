@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.QuantityFormatter
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.QuantityInputViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

class QuantityInputScreenTest {

    private interface TestQuantityInputScreen {
        fun onResult(result: QuantityInputResult)
    }

    /**
     * Simple stub implementation of QuantityFormatter for testing.
     * Returns a predictable format: "{value}" (e.g., "x2" for 2)
     */
    private class TestQuantityFormatter : QuantityFormatter {
        override fun format(quantity: QuantityIO): String = "${quantity.toInt()}"
    }

    @Test
    fun testOnResult() = runComposeUiTest {
        val test = mock<TestQuantityInputScreen> {
            every { onResult(any()) } returns Unit
        }

        val request = QuantityInputRequest(
            quantity = QuantityIO.of(2),
            minQuantity = QuantityParam.Enable(QuantityIO.of(1)),
            maxQuantity = QuantityParam.Enable(QuantityIO.of(10)),
            toolbarTitle = StringParam.Enable("Test Quantity Input"),
        )

        val viewModel = QuantityInputViewModel(request, TestQuantityFormatter())

        setContent {
            AppTheme {
                QuantityInputScreen(
                    onResult = test::onResult,
                    viewModel = viewModel,
                )
            }
        }

        onNodeWithContentDescription("Toolbar title").assertTextEquals("Test Quantity Input")

        onNodeWithContentDescription("Max allowed quantity").assertTextEquals("Max. 10")
        onNodeWithContentDescription("Current quantity").assertTextEquals("2")
        onNodeWithContentDescription("Min allowed quantity").assertTextEquals("Min. 1")

        onNodeWithContentDescription("Submit button").performClick()

        verify {
            test.onResult(
                QuantityInputResult.Success(
                    quantity = QuantityIO.of(2),
                ),
            )
        }
    }

    @Test
    fun testOnDismiss() = runComposeUiTest {
        val test = mock<TestQuantityInputScreen> {
            every { onResult(any()) } returns Unit
        }

        val request = QuantityInputRequest(
            quantity = QuantityIO.of(2),
            minQuantity = QuantityParam.Enable(QuantityIO.of(1)),
            maxQuantity = QuantityParam.Enable(QuantityIO.of(10)),
            toolbarTitle = StringParam.Enable("Test Quantity Input"),
        )

        val viewModel = QuantityInputViewModel(request, TestQuantityFormatter())

        setContent {
            AppTheme {
                QuantityInputScreen(
                    onResult = test::onResult,
                    viewModel = viewModel,
                )
            }
        }

        onNodeWithContentDescription("Toolbar back button").performClick()

        verify {
            test.onResult(QuantityInputResult.Canceled)
        }
    }
}
