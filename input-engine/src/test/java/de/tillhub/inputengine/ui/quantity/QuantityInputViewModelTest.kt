package de.tillhub.inputengine.ui.quantity

import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first

class QuantityInputViewModelTest : FunSpec({

    lateinit var inputController: NumberInputController
    lateinit var viewModel: QuantityInputViewModel

    beforeTest {
        inputController = mockk(relaxed = true) {
            every { setValue(any(), any(), any()) } just Runs
            every { value() } returns 5
        }
        viewModel = QuantityInputViewModel(inputController)
    }

    test("displayDataFlow") {
        viewModel.displayDataFlow.value shouldBe QuantityInputData.EMPTY
    }

    test("setInitialValue") {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.of(1),
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsNegatives = true,
                allowsZero = false,
            )
        )

        verify {
            inputController.setValue(
                majorDigits = listOf(Digit.ONE),
                minorDigits = emptyList(),
                isNegative = false
            )
        }

        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.of(1),
            text = "1",
            color = OrbitalBlue,
            isValid = true
        )

        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.ZERO,
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsZero = true,
            )
        )

        verify {
            inputController.setValue(
                majorDigits = listOf(Digit.ZERO),
                minorDigits = emptyList(),
                isNegative = false
            )
        }

        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.ZERO,
            text = "0",
            color = OrbitalBlue,
            isValid = true
        )
    }

    test("decrease") {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.of(1),
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsNegatives = true,
                allowsZero = false,
            )
        )
        viewModel.decrease()

        verify {
            inputController.setValue(
                majorDigits = listOf(Digit.ONE),
                minorDigits = emptyList(),
                isNegative = true
            )
        }
        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.of(-1),
            text = "-1",
            color = OrbitalBlue,
            isValid = true
        )
    }

    test("increase") {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.of(1.5),
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20))
            )
        )
        viewModel.increase()

        verify {
            inputController.setValue(
                majorDigits = listOf(Digit.TWO),
                minorDigits = emptyList(),
                isNegative = false
            )
        }
        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.of(2),
            text = "2",
            color = OrbitalBlue,
            isValid = true
        )
    }

    test("processKey") {
        viewModel.setInitialValue(
            QuantityInputRequest(
                quantity = QuantityIO.ZERO,
                quantityHint = QuantityParam.Enable(QuantityIO.of(1)),
                minQuantity = QuantityParam.Enable(QuantityIO.of(-20)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                allowsNegatives = true,
                allowsZero = false,
            )
        )

        verify {
            inputController.setValue(
                majorDigits = listOf(Digit.ZERO),
                minorDigits = emptyList(),
                isNegative = false
            )
        }

        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.ZERO,
            text = "1",
            color = MagneticGrey,
            isValid = false
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

        viewModel.displayDataFlow.first() shouldBe QuantityInputData(
            qty = QuantityIO.of(5),
            text = "5",
            color = OrbitalBlue,
            isValid = true
        )
    }
})
