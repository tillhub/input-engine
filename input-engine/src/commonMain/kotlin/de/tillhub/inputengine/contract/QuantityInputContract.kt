package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_quantity
import kotlinx.serialization.Serializable

interface QuantityInputContract {
    fun launchQuantityInput(request: QuantityInputRequest)
}

@Composable
expect fun rememberQuantityInputLauncher(
    onResult: (QuantityInputResult) -> Unit,
): QuantityInputContract

@Serializable
data class QuantityInputRequest(
    val quantity: QuantityIO = QuantityIO.ZERO,
    val allowsZero: Boolean = false,
    val toolbarTitle: String = Res.string.numpad_title_quantity.key,
    val allowDecimal: Boolean = true,
    val minQuantity: QuantityParam = QuantityParam.Disable,
    val maxQuantity: QuantityParam = QuantityParam.Disable,
    val hintQuantity: QuantityParam = QuantityParam.Disable,
    val extras: Map<String, String> = emptyMap(),
)

@Serializable
sealed class QuantityInputResult {
    @Serializable
    data class Success(
        val quantity: QuantityIO,
        val extras: Map<String, String> = emptyMap(),
    ) : QuantityInputResult()

    @Serializable
    data object Canceled : QuantityInputResult()
}
