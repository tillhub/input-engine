package de.tillhub.inputengine.ui.percentage

import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.helper.NumpadKey
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageInputViewModelTest {

    private lateinit var inputController: NumberInputController
    private lateinit var viewModel: PercentageInputViewModel

    @BeforeTest
    fun setup() {
        inputController = mock<NumberInputController>(mode = MockMode.autofill) {
            every { value() } returns 25.0
            every { minorDigits } returns emptyList()
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
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
            ),
        )
        assertEquals(
            PercentageInputData(PercentIO.of(20), "20 %", isValid = true),
            viewModel.percentageInput.first(),
        )
    }

    @Test
    fun init_zeroNotAllowed_setsInvalid() = runTest {
        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.ZERO,
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
                allowsZero = false,
            ),
        )
        assertEquals(
            PercentageInputData(PercentIO.ZERO, "0 %", isValid = false),
            viewModel.percentageInput.first(),
        )
    }

    @Test
    fun input_callsControllerAndUpdatesState() = runTest {
        // Arrange: set up expectations for input controller interactions
        every { inputController.clear() } returns Unit
        every { inputController.switchToMinor(any()) } returns Unit
        every { inputController.deleteLast() } returns Unit
        every { inputController.addDigit(any()) } returns Unit
        every { inputController.value() } returns 25.0

        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
            ),
        )

        viewModel.input(NumpadKey.Clear)
        verify { inputController.clear() }

        viewModel.input(NumpadKey.DecimalSeparator)
        verify { inputController.switchToMinor(true) }

        viewModel.input(NumpadKey.Delete)
        verify { inputController.deleteLast() }

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        verify { inputController.clear() }
        verify { inputController.addDigit(Digit.ONE) }

        assertEquals(
            PercentageInputData(PercentIO.of(25), "25 %", isValid = true),
            viewModel.percentageInput.first(),
        )
    }

    @Test
    fun input_maxOverreached_clampsToMax() = runTest {
        every { inputController.value() } returns 105.0

        viewModel.init(
            PercentageInputRequest(
                percent = PercentIO.of(20),
                percentageMin = PercentageParam.Enable(PercentIO.of(10)),
                percentageMax = PercentageParam.Enable(PercentIO.of(90)),
            ),
        )

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))

        assertEquals(
            PercentageInputData(PercentIO.of(90), "90 %", isValid = true),
            viewModel.percentageInput.first(),
        )
    }

    companion object {
        const val LOCALE = "de"
    }
}
