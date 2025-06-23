package de.tillhub.inputengine.ui.pininput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.amount.AmountInputRequest
import de.tillhub.inputengine.ui.amount.AmountInputResult
import de.tillhub.inputengine.ui.amount.AmountInputViewModel

@Composable
fun PinInputScreen(
    request: AmountInputRequest,
    viewModel: AmountInputViewModel = remember { AmountInputViewModel() },
    onResult: (AmountInputResult) -> Unit,
    onDismiss: () -> Unit
) {
}