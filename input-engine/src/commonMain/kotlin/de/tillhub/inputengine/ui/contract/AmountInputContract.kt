package de.tillhub.inputengine.ui.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.ui.amountinput.AmountInputRequest
import de.tillhub.inputengine.ui.amountinput.AmountInputResult

interface AmountInputContract {
    fun launchAmountInput(request: AmountInputRequest,)
}

@Composable
expect fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit
): AmountInputContract
