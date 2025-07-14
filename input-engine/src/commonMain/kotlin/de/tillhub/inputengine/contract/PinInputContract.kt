package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.domain.StringParam
import kotlinx.serialization.Serializable

interface PinInputContract {
    fun launchPinInput(request: PinInputRequest)
}

@Composable
expect fun rememberPinInputLauncher(onResult: (PinInputResult) -> Unit): PinInputContract

@Serializable
data class PinInputRequest(
    val pin: String,
    val toolbarTitle: StringParam = StringParam.Disable,
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
