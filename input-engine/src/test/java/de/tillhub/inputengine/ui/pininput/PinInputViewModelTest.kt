package de.tillhub.inputengine.ui.pininput

import de.tillhub.inputengine.ViewModelFunSpec
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
class PinInputViewModelTest : ViewModelFunSpec({

    lateinit var viewModel: PinInputViewModel

    beforeTest {
        viewModel = PinInputViewModel()
    }

    test("pinInputState") {
        viewModel.pinInputState.value shouldBe PinInputState.AwaitingInput
    }

    test("enteredPin") {
        viewModel.enteredPin.value.shouldBeEmpty()
    }

    test("init: Pin empty") {
        viewModel.init("")
        viewModel.pinInputState.first() shouldBe PinInputState.InvalidPinFormat
    }

    test("init: Invalid pin format") {
        viewModel.init("ABCD")
        viewModel.pinInputState.first() shouldBe PinInputState.InvalidPinFormat
    }

    test("init") {
        viewModel.init("1234")
        viewModel.pinInputState.first() shouldBe PinInputState.AwaitingInput
    }

    test("input") {
        viewModel.init("1234")
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        viewModel.enteredPin.first() shouldBe "1235"
        viewModel.pinInputState.first() shouldBe PinInputState.PinInvalid

        viewModel.input(NumpadKey.Delete)
        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))

        viewModel.enteredPin.first() shouldBe "1234"
        viewModel.pinInputState.first() shouldBe PinInputState.PinValid

        viewModel.input(NumpadKey.Clear)

        viewModel.enteredPin.first().shouldBeEmpty()
        viewModel.pinInputState.first() shouldBe PinInputState.AwaitingInput
    }
})
