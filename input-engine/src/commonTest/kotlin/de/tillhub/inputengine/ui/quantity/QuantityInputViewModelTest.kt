package de.tillhub.inputengine.ui.quantity

import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.param.QuantityParam
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuantityInputViewModelTest {

    private lateinit var inputController: NumberInputController
    private lateinit var viewModel: QuantityInputViewModel

    @BeforeTest
    fun setup() {
        inputController = mock(mode = MockMode.autofill)
        viewModel = QuantityInputViewModel(inputController)

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
                allowsZero = false,
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
                    allowsZero = false,
                ),
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
                    maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                ),
            )
            viewModel.increase()
            assertEquals(QuantityIO.of(2), viewModel.displayDataFlow.first().qty)
            assertTrue(viewModel.displayDataFlow.first().isValid)
        }

        @Test
        fun processKey() = runTest {
            every { inputController.value() } returns 5

            viewModel.setInitialValue(
                QuantityInputRequest(
                    quantity = QuantityIO.ZERO,
                    quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                    minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                    maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                    allowsZero = false,
                ),
            )

            assertEquals(
                QuantityInputData(
                    qty = QuantityIO.ZERO,
                    text = "1",
                    color = MagneticGrey,
                    isValid = false,
                ),
                viewModel.displayDataFlow.first(),
            )

            viewModel.processKey(NumpadKey.SingleDigit(Digit.ONE))
            verify { inputController.addDigit(Digit.ONE) }

            viewModel.processKey(NumpadKey.Clear)
            verify { inputController.clear() }

            viewModel.processKey(NumpadKey.Delete)
            verify { inputController.deleteLast() }

            viewModel.processKey(NumpadKey.DecimalSeparator)
            verify { inputController.switchToMinor(true) }

            viewModel.processKey(NumpadKey.Negate)
            verify { inputController.switchNegate() }

            assertEquals(
                QuantityInputData(
                    qty = QuantityIO.of(5),
                    text = "5",
                    color = OrbitalBlue,
                    isValid = true,
                ),
                viewModel.displayDataFlow.first(),
            )
        }
    }
}
