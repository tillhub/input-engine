package de.tillhub.inputengine.ui.quantity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.amountinput.AmountInputRequest
import de.tillhub.inputengine.ui.amountinput.AmountInputResult
import de.tillhub.inputengine.ui.amountinput.AmountInputViewModel

@Composable
fun QuantityInputScreen(
    request: AmountInputRequest,
    viewModel: AmountInputViewModel = remember { AmountInputViewModel() },
    onResult: (AmountInputResult) -> Unit,
    onDismiss: () -> Unit
) {
}