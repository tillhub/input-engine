@file:OptIn(ExperimentalCoroutinesApi::class)

package de.tillhub.inputengine.ui

import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.eur
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.ui.MoneyInputData.Companion.DEFAULT_CURRENCY
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

class AmountInputViewModelTest {

    /**
     * Simple stub implementation of MoneyFormatter for testing.
     * Returns a predictable format: "{currency}{amount}" (e.g., "EUR1000.0" for €10.00)
     */
    private class TestMoneyFormatter : MoneyFormatter {
        override fun format(money: MoneyIO): String = "${money.currency.isoCode}${money.toDouble()}"
    }

    private lateinit var testFormatter: MoneyFormatter

    @BeforeTest
    fun setup() {
        testFormatter = TestMoneyFormatter()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialization() = runTest {
        val request =
            AmountInputRequest(
                amount = 10.eur,
                toolbarTitle = StringParam.Enable("Test Amount Input"),
                extras = mapOf("key1" to "value1"),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            StringParam.Enable("Test Amount Input"),
            viewModel.toolbarTitle,
            "Toolbar title should match request",
        )

        assertEquals(
            mapOf("key1" to "value1"),
            viewModel.responseExtras,
            "Response extras should match request",
        )

        assertEquals(
            StringParam.Disable,
            viewModel.uiMinValue.value,
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Disable,
            viewModel.uiMaxValue.value,
            "UI max value should be disabled",
        )

        assertEquals(
            AmountInputMode.BOTH,
            viewModel.amountInputMode,
            "Amount input mode should be initialized to both",
        )

        assertEquals(
            MoneyInputData(
                money = MoneyIO.zero(DEFAULT_CURRENCY),
                text = "",
                isValid = false,
                isHint = true,
            ),
            viewModel.moneyInput.value,
            "Amount input mode should be initialized to zero",
        )

        assertEquals(
            MoneyInputData(
                money = 10.eur,
                text = "EUR1000.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun testMoneyMinGreaterThanMoneyMax() = runTest {
        val request =
            AmountInputRequest(
                amount = 5.eur,
                amountMin = MoneyParam.Enable(20.eur),
                amountMax = MoneyParam.Enable(5.eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            StringParam.Disable,
            viewModel.uiMinValue.value,
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Disable,
            viewModel.uiMaxValue.value,
            "UI max value should be disabled",
        )

        assertEquals(
            MoneyInputData(
                money = 5.eur,
                text = "EUR500.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun testMoneyMinZeroAndMoneyMaxPositive() = runTest {
        val request =
            AmountInputRequest(
                amount = 2.eur,
                amountMin = MoneyParam.Enable(0.eur),
                amountMax = MoneyParam.Enable(5.eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            AmountInputMode.POSITIVE,
            viewModel.amountInputMode,
            "Amount input mode should be initialized to both",
        )

        assertEquals(
            StringParam.Disable,
            viewModel.uiMinValue.first(),
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Enable("EUR500.0"),
            viewModel.uiMaxValue.first(),
            "UI max value should be disabled",
        )

        assertEquals(
            MoneyInputData(
                money = 2.eur,
                text = "EUR200.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun testMoneyMinPositiveAndMoneyMaxPositive() = runTest {
        val request =
            AmountInputRequest(
                amount = 0.eur,
                amountMin = MoneyParam.Enable(2.eur),
                amountMax = MoneyParam.Enable(20.eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            AmountInputMode.POSITIVE,
            viewModel.amountInputMode,
            "Amount input mode should be initialized to both",
        )

        assertEquals(
            StringParam.Enable("EUR200.0"),
            viewModel.uiMinValue.first(),
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Enable("EUR2000.0"),
            viewModel.uiMaxValue.first(),
            "UI max value should be disabled",
        )

        assertEquals(
            MoneyInputData(
                money = 0.eur,
                text = "EUR0.0",
                isValid = false,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun moneyMaxZeroAndMoneyMinNegative() = runTest {
        val request =
            AmountInputRequest(
                amount = 0.eur,
                amountMin = MoneyParam.Enable((-20).eur),
                amountMax = MoneyParam.Enable(0.eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            AmountInputMode.NEGATIVE,
            viewModel.amountInputMode,
            "Amount input mode should be initialized to both",
        )

        assertEquals(
            StringParam.Disable,
            viewModel.uiMinValue.first(),
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Enable("EUR2000.0"),
            viewModel.uiMaxValue.first(),
            "UI max value should be disabled",
        )

        assertEquals(
            MoneyInputData(
                money = 0.eur,
                text = "EUR0.0",
                isValid = false,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun testMoneyMinNegativeAndMoneyMaxNegative() = runTest {
        val request =
            AmountInputRequest(
                amount = 0.eur,
                amountMin = MoneyParam.Enable((-25).eur),
                amountMax = MoneyParam.Enable((-2).eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        assertEquals(
            AmountInputMode.NEGATIVE,
            viewModel.amountInputMode,
            "Amount input mode should be initialized to both",
        )

        assertEquals(
            StringParam.Enable("EUR200.0"),
            viewModel.uiMinValue.first(),
            "UI min value should be disabled",
        )

        assertEquals(
            StringParam.Enable("EUR2500.0"),
            viewModel.uiMaxValue.first(),
            "UI max value should be disabled",
        )

        assertEquals(
            MoneyInputData(
                money = 0.eur,
                text = "EUR0.0",
                isValid = false,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be initialized to zero",
        )
    }

    @Test
    fun testInput() = runTest {
        val request =
            AmountInputRequest(
                amount = 0.eur,
                amountMin = MoneyParam.Enable((-20).eur),
                amountMax = MoneyParam.Enable(20.eur),
            )

        val viewModel = AmountInputViewModel(request, testFormatter)

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))

        assertEquals(
            MoneyInputData(
                money = 1.23.eur,
                text = "EUR123.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be EUR1.23",
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        assertEquals(
            MoneyInputData(
                money = 20.eur,
                text = "EUR2000.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be EUR2000",
        )

        viewModel.input(NumpadKey.Negate)

        assertEquals(
            MoneyInputData(
                money = (-20).eur,
                text = "EUR-2000.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be EUR2000",
        )

        viewModel.input(NumpadKey.Delete)

        assertEquals(
            MoneyInputData(
                money = (-2).eur,
                text = "EUR-200.0",
                isValid = true,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be EUR200",
        )

        viewModel.input(NumpadKey.Clear)

        assertEquals(
            MoneyInputData(
                money = 0.eur,
                text = "EUR0.0",
                isValid = false,
                isHint = false,
            ),
            viewModel.moneyInput.first(),
            "Amount input mode should be EUR200",
        )
    }
}
