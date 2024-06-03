package de.tillhub.inputengine.ui.percentage

import de.tillhub.inputengine.ViewModelFunSpec
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.helper.NumberInputController
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import java.util.Locale

@ExperimentalCoroutinesApi
class PercentageInputViewModelTest : ViewModelFunSpec({

    lateinit var inputController: NumberInputController
    lateinit var viewModel: PercentageInputViewModel

    beforeTest {
        inputController = mockk(relaxed = true) {
            every { value() } returns 25
        }
        viewModel = PercentageInputViewModel(inputController, Locale.GERMANY)
    }

    test("percentageInput") {
        viewModel.percentageInput.value shouldBe PercentageInputData.EMPTY
    }

    test("init") {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )
        inputController.setValue(20.0)
        viewModel.percentageInput.first() shouldBe PercentageInputData(
            percent = PercentIO.of(20),
            text = "20 %",
            isValid = true
        )
    }

    test("init: zero not allowed") {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.ZERO,
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
                allowsZero = false
            )
        )

        viewModel.percentageInput.first() shouldBe PercentageInputData(
            percent = PercentIO.ZERO,
            text = "0 %",
            isValid = false
        )
    }

    test("input") {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )
        viewModel.input(NumpadKey.Clear)
        verify {
            inputController.clear()
        }

        viewModel.input(NumpadKey.DecimalSeparator)
        verify {
            inputController.switchToMinor(true)
        }

        viewModel.input(NumpadKey.Delete)
        verify {
            inputController.deleteLast()
        }

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        verify {
            inputController.clear()
            inputController.addDigit(Digit.ONE)
        }

        viewModel.percentageInput.first() shouldBe PercentageInputData(
            percent = PercentIO.of(25),
            text = "25 %",
            isValid = true
        )
    }

    test("input: max overreached") {
        every { inputController.value() } returns 105

        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90))
            )
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))

        viewModel.percentageInput.first() shouldBe PercentageInputData(
            percent = PercentIO.of(90),
            text = "90 %",
            isValid = true
        )
    }
})
