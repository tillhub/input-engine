package de.tillhub.inputengine.ui.quantity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.ui.amount.AmountInputViewModel

@Composable
fun QuantityInputScreen(
    request: AmountInputRequest,
    viewModel: AmountInputViewModel = remember { AmountInputViewModel() },
    onResult: (AmountInputResult) -> Unit,
    onDismiss: () -> Unit
) {
}