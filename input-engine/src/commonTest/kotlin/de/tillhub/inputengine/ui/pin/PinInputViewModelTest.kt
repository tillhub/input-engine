package de.tillhub.inputengine.ui.pin

import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.ui.pininput.PinInputState
import de.tillhub.inputengine.ui.pininput.PinInputViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PinInputViewModelTest {

    private lateinit var viewModel: PinInputViewModel
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = PinInputViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun pinInputState_defaultsToAwaitingInput() {
        assertEquals(PinInputState.AwaitingInput, viewModel.pinInputState.value)
    }

    @Test
    fun enteredPin_defaultsToEmpty() {
        assertTrue(viewModel.enteredPin.value.isEmpty())
    }

    @Test
    fun init_withEmptyPin_setsInvalidPinFormat() = runTest {
        viewModel.init("")
        assertEquals(PinInputState.InvalidPinFormat, viewModel.pinInputState.first())
    }

    @Test
    fun init_withInvalidPinFormat_setsInvalidPinFormat() = runTest {
        viewModel.init("ABCD")
        assertEquals(PinInputState.InvalidPinFormat, viewModel.pinInputState.first())
    }

    @Test
    fun init() = runTest {
        viewModel.init("1234")
        assertEquals(PinInputState.AwaitingInput, viewModel.pinInputState.first())
    }

    @Test
    fun input() = runTest {
        viewModel.init("1234")

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        assertEquals("1235", viewModel.enteredPin.first())
        assertEquals(PinInputState.PinInvalid, viewModel.pinInputState.first())

        viewModel.input(NumpadKey.Delete)
        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))

        assertEquals("1234", viewModel.enteredPin.first())
        assertEquals(PinInputState.PinValid, viewModel.pinInputState.first())

        viewModel.input(NumpadKey.Clear)

        assertTrue(viewModel.enteredPin.first().isEmpty())
        assertEquals(PinInputState.AwaitingInput, viewModel.pinInputState.first())
    }
}
