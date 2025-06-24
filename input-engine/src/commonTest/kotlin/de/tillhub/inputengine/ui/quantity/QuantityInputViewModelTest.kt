package de.tillhub.inputengine.ui.quantity

import com.ionspin.kotlin.bignum.BigNumber
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.param.QuantityParam
import de.tillhub.inputengine.helper.NumberInputControllerContract
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuantityInputViewModelTest {

    private lateinit var inputController: FakeNumberInputController
    private lateinit var viewModel: QuantityInputViewModel

    @BeforeTest
    fun setup() {
        inputController = FakeNumberInputController()
        viewModel = QuantityInputViewModel(inputController)
    }

    @Test
    fun displayDataFlow() = runTest {
        assertEquals(QuantityInputData.EMPTY, viewModel.displayDataFlow.value)
    }

    @Test
    fun setInitialValue() = runTest {
        val request = QuantityInputRequest(
            quantity = QuantityIO.of(1),
            quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
            minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
            maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
            allowsZero = false
        )
        viewModel.setInitialValue(request)

        assertEquals(QuantityIO.of(1), viewModel.displayDataFlow.first().qty)
        assertTrue(viewModel.displayDataFlow.first().isValid)

        val zeroRequest = request.copy(quantity = QuantityIO.ZERO, allowsZero = true)
        viewModel.setInitialValue(zeroRequest)

        assertEquals(QuantityIO.ZERO, viewModel.displayDataFlow.first().qty)
        assertTrue(viewModel.displayDataFlow.first().isValid)
    }

    @Test
    fun decrease() = runTest {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.of(1),
                quantityHint = QuantityParam.Disable,
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsZero = false
            )
        )
        viewModel.decrease()
        assertEquals(QuantityIO.of(-1), viewModel.displayDataFlow.first().qty)
        assertTrue(viewModel.displayDataFlow.first().isValid)
    }

    @Test
    fun increase() = runTest {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.of(1.5),
                quantityHint = QuantityParam.Disable,
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20))
            )
        )
        viewModel.increase()
        assertEquals(QuantityIO.of(2), viewModel.displayDataFlow.first().qty)
        assertTrue(viewModel.displayDataFlow.first().isValid)
    }

    @Test
    fun processKey() = runTest {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.ZERO,
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsZero = false
            )
        )

        assertEquals(
            QuantityInputData(
                qty = QuantityIO.ZERO,
                text = "1", // hint fallback
                color = MagneticGrey,
                isValid = false
            ),
            viewModel.displayDataFlow.first()
        )

        viewModel.processKey(NumpadKey.SingleDigit(Digit.ONE))
        assertEquals(Digit.ONE, inputController.lastAddedDigit)

        viewModel.processKey(NumpadKey.Clear)
        assertTrue(inputController.clearCalled)

        viewModel.processKey(NumpadKey.Delete)
        assertTrue(inputController.deleteCalled)

        viewModel.processKey(NumpadKey.DecimalSeparator)
        assertTrue(inputController.switchToMinorCalled)

        viewModel.processKey(NumpadKey.Negate)
        assertTrue(inputController.switchNegateCalled)

        // Ensure final value
        assertEquals(
            QuantityInputData(
                qty = QuantityIO.of(5),
                text = "5",
                color = OrbitalBlue,
                isValid = true
            ),
            viewModel.displayDataFlow.first()
        )
    }

    class FakeNumberInputController : NumberInputControllerContract {
        var clearCalled = false
        var deleteCalled = false
        var switchToMinorCalled = false
        var switchNegateCalled = false
        var lastAddedDigit: Digit? = null

        override fun clear() {
            clearCalled = true
        }

        override fun switchToMinor(enabled: Boolean) {
            switchToMinorCalled = enabled
        }

        override fun switchNegate() {
            switchNegateCalled = true
        }

        override fun setValue(
            majorDigits: List<Digit>,
            minorDigits: List<Digit>,
            isNegative: Boolean
        ) = Unit

        override fun setValue(number: Number) = Unit

        override fun setValue(number: BigNumber<*>) = Unit

        override fun deleteLast() {
            deleteCalled = true
        }

        override val minorDigits: List<Digit>
            get() = emptyList()

        override fun addDigit(digit: Digit) {
            lastAddedDigit = digit
        }

        override fun value(): Number = 5
    }
}
