package de.tillhub.inputengine.ui.pininput

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.data.NumpadKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

class PinInputViewModel : ViewModel() {

    private lateinit var pin: String

    private val _pinInputState: MutableStateFlow<PinInputState> =
        MutableStateFlow(PinInputState.AwaitingInput)
    val pinInputState: StateFlow<PinInputState> = _pinInputState

    private val _pinInputChars = MutableStateFlow(emptyList<Char>())
    val enteredPin: StateFlow<String> = _pinInputChars.map { chars ->
        chars.joinToString("").apply {
            validateStringInput(this)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

    fun init(pin: String) {
        this.pin = pin
    }

    private fun validateStringInput(pinInput: CharSequence) {
        when {
            isPinValid(pinInput) -> {
                _pinInputState.value = PinInputState.PinValid
            }

            pinInput.length > pin.length -> {
                _pinInputState.value = PinInputState.PinTooLong
            }

            else -> {
                _pinInputState.value = PinInputState.AwaitingInput
            }
        }
    }

    private fun isPinValid(pinInput: CharSequence): Boolean {
        return this.pin == pinInput
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> {
                _pinInputChars.value = emptyList()
            }

            NumpadKey.Delete -> {
                _pinInputChars.value = _pinInputChars.value.toMutableList().dropLast(1)
            }

            is NumpadKey.SingleDigit -> {
                _pinInputChars.value = _pinInputChars.value.toMutableList()
                    .apply { add(key.digit.value.digitToChar()) }
            }
        }
    }
}

sealed class PinInputState {
    data object AwaitingInput : PinInputState()
    data object PinValid : PinInputState()
    data object PinTooLong : PinInputState()
}

enum class FormatType {
    ALPHANUMERIC,
    NUMERIC,
    ALPHABETIC
}

@Parcelize
sealed class InputResultStatus : Parcelable {
    data class Success(
        val amount: BigDecimal,
        val extras: Bundle
    ) : InputResultStatus()

    data object Cancel : InputResultStatus()
}