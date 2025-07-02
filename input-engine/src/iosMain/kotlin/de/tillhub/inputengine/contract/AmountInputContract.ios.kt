package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.AmountInputPresenter

@Composable
actual fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit,
): AmountInputContract = remember {
    val presenter = AmountInputPresenter(onResult)

    object : AmountInputContract {
        override fun launchAmountInput(request: AmountInputRequest) {
            presenter.launch(request)
        }
    }
}
