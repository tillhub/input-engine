package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_percentage
import kotlinx.serialization.Serializable

interface PercentageInputContract {
    fun launchPercentageInput(request: PercentageInputRequest)
}

@Composable
expect fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit,
): PercentageInputContract

@Serializable
data class PercentageInputRequest(
    val percent: PercentIO = PercentIO.ZERO,
    val allowsZero: Boolean = false,
    val toolbarTitle: String = Res.string.numpad_title_percentage.key,
    val allowDecimal: Boolean = false,
    val percentageMin: PercentageParam = PercentageParam.Disable,
    val percentageMax: PercentageParam = PercentageParam.Disable,
    val extras: Map<String, String> = emptyMap(),
)

@Serializable
sealed class PercentageInputResult {
    @Serializable
    data class Success(
        val percent: PercentIO,
        val extras: Map<String, String> = emptyMap(),
    ) : PercentageInputResult()

    @Serializable
    data object Canceled : PercentageInputResult()
}
