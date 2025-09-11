@file:OptIn(ExperimentalCoroutinesApi::class)

package de.tillhub.inputengine.ui

import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
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

class PinInputViewModelTest {

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialization() = runTest {
        val request =
            PinInputRequest(
                pin = "1234",
                toolbarTitle = StringParam.Enable("Test Pin Input"),
                overridePinInput = true,
                extras = mapOf("key1" to "value1"),
            )

        val viewModel = PinInputViewModel(request)

        assertEquals(
            StringParam.Enable("Test Pin Input"),
            viewModel.toolbarTitle,
            "Toolbar title should match request",
        )

        assertEquals(
            mapOf("key1" to "value1"),
            viewModel.responseExtras,
            "Response extras should match request",
        )

        assertTrue(viewModel.overridePinInput, "Override pin input should be true")

        assertEquals(
            "••••",
            viewModel.hint,
            "Hint should be set to '••••'",
        )

        assertEquals(
            PinInputState.AwaitingInput,
            viewModel.pinInputState.first(),
            "Pin input state should be AwaitingInput",
        )

        assertEquals(
            "",
            viewModel.enteredPin.first(),
            "Entered pin should be empty",
        )
    }

    @Test
    fun testEmptyPinShouldBeInvalid() = runTest {
        val request =
            PinInputRequest(
                pin = "",
            )

        val viewModel = PinInputViewModel(request)

        assertEquals(
            PinInputState.InvalidPinFormat,
            viewModel.pinInputState.first(),
            "Pin input state should be InvalidPinFormat",
        )
    }

    @Test
    fun testNonNumericPinShouldBeInvalid() = runTest {
        val request =
            PinInputRequest(
                pin = "1ab3",
            )

        val viewModel = PinInputViewModel(request)

        assertEquals(
            PinInputState.InvalidPinFormat,
            viewModel.pinInputState.first(),
            "Pin input state should be InvalidPinFormat",
        )
    }

    @Test
    fun testInput() = runTest {
        val request =
            PinInputRequest(
                pin = "1234",
            )

        val viewModel = PinInputViewModel(request)

        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        assertEquals(
            "1235",
            viewModel.enteredPin.first(),
            "Entered pin should be 1235",
        )

        assertEquals(
            PinInputState.PinInvalid,
            viewModel.pinInputState.first(),
            "Pin input state should be PinInvalid",
        )

        viewModel.input(NumpadKey.Delete)
        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))

        assertEquals(
            "1234",
            viewModel.enteredPin.first(),
            "Entered pin should be 1235",
        )

        assertEquals(
            PinInputState.PinValid,
            viewModel.pinInputState.first(),
            "Pin input state should be PinValid",
        )

        viewModel.input(NumpadKey.Clear)

        assertTrue(viewModel.enteredPin.first().isEmpty(), "Entered pin should be empty")
        assertEquals(
            PinInputState.AwaitingInput,
            viewModel.pinInputState.first(),
            "Pin input state should be AwaitingInput",
        )
    }
}
