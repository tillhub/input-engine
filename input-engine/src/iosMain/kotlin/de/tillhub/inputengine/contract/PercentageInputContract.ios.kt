package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.percentage.PercentageInputPresenter

@Composable
actual fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit
): PercentageInputContract = remember {
    val presenter = PercentageInputPresenter(onResult)

    object : PercentageInputContract {
        override fun launchPercentageInput(request: PercentageInputRequest) {
            presenter.launch(request)
        }
    }
}

