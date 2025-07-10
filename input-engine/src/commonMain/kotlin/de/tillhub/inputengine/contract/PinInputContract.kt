package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_pin
import kotlinx.serialization.Serializable

interface PinInputContract {
    fun launchPinInput(request: PinInputRequest)
}

@Composable
expect fun rememberPinInputLauncher(onResult: (PinInputResult) -> Unit): PinInputContract

@Serializable
data class PinInputRequest(
    val pin: String,
    val toolbarTitle: String = Res.string.numpad_title_pin.key,
    val overridePinInput: Boolean = false,
    val extras: Map<String, String> = emptyMap(),
)

@Serializable
sealed class PinInputResult {
    @Serializable
    data class Success(
        val extras: Map<String, String>,
    ) : PinInputResult()

    @Serializable
    data object Canceled : PinInputResult()
}
