package de.tillhub.inputengine.ui.percentage

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.helper.NumberInputControllerContract
import de.tillhub.inputengine.helper.NumpadKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PercentageInputViewModelTest {

    private lateinit var inputController: FakeNumberInputController
    private lateinit var viewModel: PercentageInputViewModel

    @BeforeTest
    fun setup() {
        inputController = FakeNumberInputController().apply {
            valueOverride = 25.0
        }
        viewModel = PercentageInputViewModel(inputController, LOCALE)
    }

    @Test
    fun percentageInput_isEmptyInitially() = runTest {
        assertEquals(PercentageInputData.EMPTY, viewModel.percentageInput.value)
    }

    @Test
    fun init_setsValidPercentageData() = runTest {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )
        assertEquals(
            PercentageInputData(PercentIO.of(20), "20 %", isValid = true),
            viewModel.percentageInput.first()
        )
    }

    @Test
    fun init_zeroNotAllowed_setsInvalid() = runTest {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.ZERO,
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
                allowsZero = false
            )
        )
        assertEquals(
            PercentageInputData(PercentIO.ZERO, "0 %", isValid = false),
            viewModel.percentageInput.first()
        )
    }

    @Test
    fun input_callsControllerAndUpdatesState() = runTest {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )

        viewModel.input(NumpadKey.Clear)
        assertTrue(inputController.clearCalled)

        viewModel.input(NumpadKey.DecimalSeparator)
        assertTrue(inputController.switchToMinorCalled)

        viewModel.input(NumpadKey.Delete)
        assertTrue(inputController.deleteCalled)

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        assertTrue(inputController.clearCalledAfterInit)
        assertEquals(Digit.ONE, inputController.lastAddedDigit)

        assertEquals(
            PercentageInputData(PercentIO.of(25), "25 %", isValid = true),
            viewModel.percentageInput.first()
        )
    }

    @Test
    fun input_maxOverreached_clampsToMax() = runTest {
        inputController.valueOverride = 105.0

        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))

        assertEquals(
            PercentageInputData(PercentIO.of(90), "90 %", isValid = true),
            viewModel.percentageInput.first()
        )
    }
    class FakeNumberInputController : NumberInputControllerContract {
        var clearCalled = false
        var clearCalledAfterInit = false
        var deleteCalled = false
        var switchToMinorCalled = false
        var lastAddedDigit: Digit? = null
        var valueOverride: Number = 0

        override fun clear() {
            clearCalled = true
            clearCalledAfterInit = true
        }

        override fun switchToMinor(enabled: Boolean) {
            switchToMinorCalled = enabled
        }

        override fun switchNegate() {}
        override fun deleteLast() {
            deleteCalled = true
        }

        override fun addDigit(digit: Digit) {
            lastAddedDigit = digit
        }

        override fun setValue(majorDigits: List<Digit>, minorDigits: List<Digit>, isNegative: Boolean) {}
        override fun setValue(number: Number) {}
        override fun setValue(number: BigNumber<*>) {}

        override val minorDigits: List<Digit> = emptyList()
        override fun value(): Number = valueOverride
    }
    companion object {
        const val LOCALE = "de"
    }
}
