package de.tillhub.inputengine.ui.moneyinput

import de.tillhub.inputengine.ViewModelFunSpec
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.helper.EUR
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
class MoneyInputViewModelTest : ViewModelFunSpec({

    lateinit var viewModel: MoneyInputViewModel

    beforeTest {
        viewModel = MoneyInputViewModel()
    }

    test("amountInputMode") {
        viewModel.amountInputMode shouldBe AmountInputMode.BOTH
    }

    test("moneyInput") {
        viewModel.moneyInput.value shouldBe MoneyInputData.EMPTY
    }

    test("uiMinValue") {
        viewModel.uiMinValue.value shouldBe MoneyParam.Disable
    }

    test("uiMaxValue") {
        viewModel.uiMaxValue.value shouldBe MoneyParam.Disable
    }

    test("init: Normal") {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                isZeroAllowed = true,
                amountMin = MoneyParam.Enable(MoneyIO.of(-2000, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(2000, EUR))
            )
        )

        viewModel.amountInputMode shouldBe AmountInputMode.BOTH
        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(400, EUR),
            text = "4,00 €",
            isValid = true
        )
        viewModel.uiMinValue.value shouldBe MoneyParam.Enable(MoneyIO.of(-2000, EUR))
        viewModel.uiMaxValue.value shouldBe MoneyParam.Enable(MoneyIO.of(2000, EUR))
    }

    test("init: min > max") {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                isZeroAllowed = true,
                amountMin = MoneyParam.Enable(MoneyIO.of(900, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(500, EUR))
            )
        )

        viewModel.amountInputMode shouldBe AmountInputMode.BOTH
        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(400, EUR),
            text = "4,00 €",
            isValid = true
        )
        viewModel.uiMinValue.value shouldBe MoneyParam.Disable
        viewModel.uiMaxValue.value shouldBe MoneyParam.Disable
    }

    test("init: Input mode is Negative") {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(-400, EUR),
                amountMin = MoneyParam.Enable(MoneyIO.of(-900, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.zero(EUR))
            )
        )

        viewModel.amountInputMode shouldBe AmountInputMode.NEGATIVE
        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(-400, EUR),
            text = "4,00 €",
            isValid = true
        )
        viewModel.uiMinValue.value shouldBe MoneyParam.Disable
        viewModel.uiMaxValue.value shouldBe MoneyParam.Enable(MoneyIO.of(900, EUR))
    }

    test("init: Input mode is Positive") {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                amountMin = MoneyParam.Enable(MoneyIO.zero(EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(900, EUR))
            )
        )

        viewModel.amountInputMode shouldBe AmountInputMode.POSITIVE
        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(400, EUR),
            text = "4,00 €",
            isValid = true
        )
        viewModel.uiMinValue.value shouldBe MoneyParam.Disable
        viewModel.uiMaxValue.value shouldBe MoneyParam.Enable(MoneyIO.of(900, EUR))
    }

    test("input") {
        viewModel.init(
            AmountInputRequest(
                amount = MoneyIO.of(400, EUR),
                isZeroAllowed = false,
                amountMin = MoneyParam.Enable(MoneyIO.of(-2000, EUR)),
                amountMax = MoneyParam.Enable(MoneyIO.of(2000, EUR))
            )
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))

        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(123, EUR),
            text = "1,23 €",
            isValid = true
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(2000, EUR),
            text = "20,00 €",
            isValid = true
        )

        viewModel.input(NumpadKey.Negate)

        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(-2000, EUR),
            text = "-20,00 €",
            isValid = true
        )

        viewModel.input(NumpadKey.Delete)

        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.of(-200, EUR),
            text = "-2,00 €",
            isValid = true
        )

        viewModel.input(NumpadKey.Clear)

        viewModel.moneyInput.first() shouldBe MoneyInputData(
            money = MoneyIO.zero(EUR),
            text = "0,00 €",
            isValid = false
        )
    }
})
