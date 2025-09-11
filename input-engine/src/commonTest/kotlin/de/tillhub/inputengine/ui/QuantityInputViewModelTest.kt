@file:OptIn(ExperimentalCoroutinesApi::class)

package de.tillhub.inputengine.ui

import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.QuantityFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuantityInputViewModelTest {

    /**
     * Simple stub implementation of QuantityFormatter for testing.
     * Returns a predictable format: "x{value}" (e.g., "x2" for 2)
     */
    private class TestQuantityFormatter : QuantityFormatter {
        override fun format(quantity: QuantityIO): String = "x${quantity.toDouble()}"
    }

    private lateinit var testFormatter: QuantityFormatter

    @BeforeTest
    fun setup() {
        testFormatter = TestQuantityFormatter()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialization() = runTest {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.of(2),
                toolbarTitle = StringParam.Enable("Test Quantity Input"),
                allowsZero = true,
                allowDecimal = true,
                minQuantity = QuantityParam.Enable(QuantityIO.ZERO),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                extras = mapOf("key1" to "value1"),
            )

        val viewModel = QuantityInputViewModel(request, testFormatter)

        assertEquals(
            StringParam.Enable("Test Quantity Input"),
            viewModel.toolbarTitle,
            "Toolbar title should match request",
        )

        assertEquals(
            mapOf("key1" to "value1"),
            viewModel.responseExtras,
            "Response extras should match request",
        )

        assertTrue(viewModel.allowDecimal, "Allows decimal should be true")

        assertFalse(viewModel.allowNegative, "Allows negative should be false")

        assertEquals(
            StringParam.Enable("x0.0"),
            viewModel.minStringParam,
            "Min string param should be set to 'x0.0'",
        )

        assertEquals(
            StringParam.Enable("x20.0"),
            viewModel.maxStringParam,
            "Max string param should be set to 'x20.0'",
        )

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(2),
                text = "x2.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input mode should be initialized to both",
        )
    }

    @Test
    fun testDecrease() = runTest {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.of(1),
                hintQuantity = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsZero = false,
            )

        val viewModel = QuantityInputViewModel(request, testFormatter)

        viewModel.decrease()

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(-1),
                text = "x-1.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be -1",
        )
    }

    @Test
    fun testIncrease() = runTest {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.of(1.5),
                hintQuantity = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
            )

        val viewModel = QuantityInputViewModel(request, testFormatter)

        viewModel.increase()

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(2),
                text = "x2.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be 2",
        )
    }

    @Test
    fun testProcessKey() = runTest {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.ZERO,
                hintQuantity = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(30)),
                allowsZero = false,
            )

        val viewModel = QuantityInputViewModel(request, testFormatter)

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.ZERO,
                text = "x1.0",
                isValid = false,
                isHint = true,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be hint 1",
        )

        viewModel.processKey(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.processKey(NumpadKey.SingleDigit(Digit.FIVE))

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(25),
                text = "x25.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be 25",
        )

        viewModel.processKey(NumpadKey.Delete)

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(2),
                text = "x2.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be 2",
        )

        viewModel.processKey(NumpadKey.DecimalSeparator)
        viewModel.processKey(NumpadKey.Negate)
        viewModel.processKey(NumpadKey.SingleDigit(Digit.THREE))

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(-2.3),
                text = "x-2.3",
                isValid = true,
                isHint = false,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be -2.3",
        )

        viewModel.processKey(NumpadKey.Clear)

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.ZERO,
                text = "x1.0",
                isValid = false,
                isHint = true,
            ),
            viewModel.quantityInputFlow.first(),
            "Quantity input flow should be hint 1",
        )
    }
}
