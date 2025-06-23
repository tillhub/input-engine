package de.tillhub.inputengine.ui.pininput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.helper.NumpadKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PinInputViewModel : ViewModel() {

    private lateinit var pin: String

    private val _pinInputState = MutableStateFlow<PinInputState>(PinInputState.AwaitingInput)
    val pinInputState: StateFlow<PinInputState> = _pinInputState

    private val _pinInputChars = MutableStateFlow<List<Char>>(emptyList())
    val enteredPin: StateFlow<String> = _pinInputChars.map { chars ->
        val pinInput = chars.joinToString("")
        validateInput(pinInput)
        pinInput
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    fun init(pin: String) {
        this.pin = pin
        if (!isPinFormatValid(pin)) {
            _pinInputState.value = PinInputState.InvalidPinFormat
        }
    }

    fun input(key: NumpadKey) {
        _pinInputChars.value = when (key) {
            NumpadKey.Clear -> emptyList()
            NumpadKey.Delete -> _pinInputChars.value.dropLast(1)
            is NumpadKey.SingleDigit -> _pinInputChars.value + key.digit.value.digitToChar()
            else -> _pinInputChars.value
        }
    }

    private fun validateInput(pinInput: String) {
        _pinInputState.value = when {
            pinInput == pin -> PinInputState.PinValid
            pinInput.length >= pin.length -> PinInputState.PinInvalid
            else -> PinInputState.AwaitingInput
        }
    }

    private fun isPinFormatValid(pin: String): Boolean =
        pin.isNotBlank() && pin.all { it.isDigit() }
}

sealed class PinInputState {
    data object AwaitingInput : PinInputState()
    data object InvalidPinFormat : PinInputState()
    data object PinValid : PinInputState()
    data object PinInvalid : PinInputState()
}
