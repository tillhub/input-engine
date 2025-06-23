package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.ui.amount.AmountInputRequest
import de.tillhub.inputengine.ui.amount.AmountInputResult

interface AmountInputContract {
    fun launchAmountInput(request: AmountInputRequest,)
}

@Composable
expect fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit
): AmountInputContract
