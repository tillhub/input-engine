package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.ui.percentage.PercentageInputRequest
import de.tillhub.inputengine.ui.percentage.PercentageInputResult

interface PercentageInputContract {
    fun launchPercentageInput(request: PercentageInputRequest)
}

@Composable
expect fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit
): PercentageInputContract
