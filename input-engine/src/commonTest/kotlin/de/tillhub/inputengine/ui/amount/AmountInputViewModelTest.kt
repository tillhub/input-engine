package de.tillhub.inputengine.ui.amount

import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.EUR
import de.tillhub.inputengine.financial.param.MoneyParam
import de.tillhub.inputengine.helper.NumpadKey
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

@OptIn(ExperimentalCoroutinesApi::class)
class AmountInputViewModelTest {

    private lateinit var viewModel: AmountInputViewModel
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = AmountInputViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test fun amountInputMode_is_initially_both() = assertEquals(AmountInputMode.BOTH, viewModel.amountInputMode)

    @Test fun moneyInput_initial_state_is_empty() = assertEquals(MoneyInputData.EMPTY, viewModel.moneyInput.value)

    @Test fun uiMinMax_are_disabled_initially() {
        assertEquals(MoneyParam.Disable, viewModel.uiMinValue.value)
        assertEquals(MoneyParam.Disable, viewModel.uiMaxValue.value)
    }

    @Test fun init_sets_mode_and_bounds_normally() = runTest {
        val request = AmountInputRequest(
            amount = MoneyIO.of(400, EUR),
            isZeroAllowed = true,
            amountMin = MoneyParam.Enable(MoneyIO.of(-2000, EUR)),
            amountMax = MoneyParam.Enable(MoneyIO.of(2000, EUR)),
        )
        viewModel.init(request)

        assertEquals(AmountInputMode.BOTH, viewModel.amountInputMode)
        assertEquals(request.amountMin, viewModel.uiMinValue.value)
        assertEquals(request.amountMax, viewModel.uiMaxValue.value)
        assertEquals(MoneyIO.of(400, EUR), viewModel.moneyInput.first().money)
        assertTrue(viewModel.moneyInput.first().isValid)
    }

    @Test fun init_when_min_greater_than_max_resets_limits() = runTest {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                isZeroAllowed = true,
                amountMin = MoneyParam.Enable(MoneyIO.of(900, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(500, EUR)),
            ),
        )
        assertEquals(MoneyParam.Disable, viewModel.uiMinValue.value)
        assertEquals(MoneyParam.Disable, viewModel.uiMaxValue.value)
    }

    @Test fun input_negate_delete_clear_flow() = runTest {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                isZeroAllowed = false,
                amountMin = MoneyParam.Enable(MoneyIO.of(-2000, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(2000, EUR)),
            ),
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))
        assertEquals(MoneyIO.of(123, EUR), viewModel.moneyInput.first().money)

        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))
        assertEquals(MoneyIO.of(2000, EUR), viewModel.moneyInput.first().money)

        viewModel.input(NumpadKey.Negate)
        assertEquals(MoneyIO.of(-2000, EUR), viewModel.moneyInput.first().money)

        viewModel.input(NumpadKey.Delete)
        assertEquals(MoneyIO.of(-200, EUR), viewModel.moneyInput.first().money)

        viewModel.input(NumpadKey.Clear)
        assertEquals(MoneyIO.zero(EUR), viewModel.moneyInput.first().money)
        assertFalse(viewModel.moneyInput.first().isValid)
    }

    @Test fun input_mode_switches_to_positive_when_min_zero_max_positive() = runTest {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(-500, EUR),
                isZeroAllowed = true,
                amountMin = MoneyParam.Enable(MoneyIO.zero(EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(1000, EUR)),
            ),
        )
        assertEquals(AmountInputMode.POSITIVE, viewModel.amountInputMode)
        assertEquals(MoneyParam.Disable, viewModel.uiMinValue.value)
        assertEquals(MoneyIO.of(500, EUR), viewModel.moneyInput.first().money.abs())
    }

    @Test fun input_mode_is_negative_when_min_and_max_negative() = runTest {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(-500, EUR),
                isZeroAllowed = true,
                amountMin = MoneyParam.Enable(MoneyIO.of(-1000, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(-200, EUR)),
            ),
        )
        assertEquals(AmountInputMode.NEGATIVE, viewModel.amountInputMode)
        assertEquals(MoneyIO.of(200, EUR), (viewModel.uiMinValue.value as MoneyParam.Enable).amount)
        assertEquals(MoneyIO.of(1000, EUR), (viewModel.uiMaxValue.value as MoneyParam.Enable).amount)
        assertEquals(MoneyIO.of(-500, EUR), viewModel.moneyInput.first().money)
    }

    @Test fun init_with_zero_and_zeroNotAllowed_sets_invalid() = runTest {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.zero(EUR),
                isZeroAllowed = false,
                amountMin = MoneyParam.Enable(MoneyIO.of(-2000, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(2000, EUR)),
            ),
        )
        assertFalse(viewModel.moneyInput.first().isValid)
    }
}
