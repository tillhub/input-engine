package de.tillhub.inputengine.ui.pininput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.domain.NumpadKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PinInputViewModel(
    private val request: PinInputRequest,
) : ViewModel() {

    val toolbarTitle: String get() = request.toolbarTitle
    val responseExtras: Map<String, String> get() = request.extras
    val overridePinInput: Boolean get() = request.overridePinInput
    val hint: String = "•".repeat(request.pin.length)

    private val _pinInputState: MutableStateFlow<PinInputState> =
        MutableStateFlow(PinInputState.AwaitingInput)
    val pinInputState: StateFlow<PinInputState> = _pinInputState

    private val pinInputChars = MutableStateFlow(emptyList<Char>())
    val enteredPin: StateFlow<String> = pinInputChars.map { chars ->
        chars.joinToString("").apply {
            validateStringInput(this)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = "",
    )

    init {
        if (!isPinFormatValid(request.pin)) {
            _pinInputState.value = PinInputState.InvalidPinFormat
        }
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> {
                pinInputChars.value = emptyList()
            }

            NumpadKey.Delete -> {
                pinInputChars.value = pinInputChars.value.toMutableList().dropLast(1)
            }

            is NumpadKey.SingleDigit -> {
                pinInputChars.value = pinInputChars.value.toMutableList()
                    .apply { add(key.digit.value.digitToChar()) }
            }

            is NumpadKey.DecimalSeparator -> Unit
            NumpadKey.Negate -> Unit
        }
    }

    private fun validateStringInput(pinInput: CharSequence) {
        when {
            isPinValid(pinInput) -> {
                _pinInputState.value = PinInputState.PinValid
            }

            pinInput.length >= request.pin.length -> {
                _pinInputState.value = PinInputState.PinInvalid
            }

            else -> {
                _pinInputState.value = PinInputState.AwaitingInput
            }
        }
    }

    private fun isPinValid(pinInput: CharSequence): Boolean = request.pin == pinInput

    private fun isPinFormatValid(pin: String): Boolean = pin.isNotEmpty() && pin.all { it.isDigit() }

    companion object {
        // Define a custom keys for our dependency
        val REQUEST_KEY = object : CreationExtras.Key<PinInputRequest> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val request = this[REQUEST_KEY] as PinInputRequest
                PinInputViewModel(request)
            }
        }
    }
}

internal sealed class PinInputState {
    data object AwaitingInput : PinInputState()
    data object InvalidPinFormat : PinInputState()
    data object PinValid : PinInputState()
    data object PinInvalid : PinInputState()
}
