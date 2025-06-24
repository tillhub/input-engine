package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.quantity.QuantityInputPresenter

@Composable
actual fun rememberQuantityInputLauncher(
    onResult: (QuantityInputResult) -> Unit
): QuantityInputContract = remember {
    val presenter = QuantityInputPresenter(onResult)

    object : QuantityInputContract {
        override fun launchQuantityInput(request: QuantityInputRequest) {
            presenter.launch(request)
        }
    }
}
