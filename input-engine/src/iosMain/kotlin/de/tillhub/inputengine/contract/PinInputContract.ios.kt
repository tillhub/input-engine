package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.tillhub.inputengine.ui.PinInputPresenter

@Composable
actual fun rememberPinInputLauncher(
    onResult: (PinInputResult) -> Unit,
): PinInputContract = remember {
    val presenter = PinInputPresenter(onResult)

    object : PinInputContract {
        override fun launchPinInput(request: PinInputRequest) {
            presenter.launch(request)
        }
    }
}
