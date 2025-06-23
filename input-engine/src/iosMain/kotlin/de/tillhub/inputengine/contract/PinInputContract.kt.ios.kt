package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@Composable
actual fun rememberPinInputLauncher(
    onResult: (PinInputResult) -> Unit
): PinInputContract = remember {
    object : PinInputContract {
        private var controller: UIViewController? = null

        override fun launchPinInput(request: PinInputRequest) {
            val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

            controller = ComposeUIViewController {
                PinInputScreen(
                    request = request,
                    onResult = {
                        onResult(it)
                        controller?.dismissViewControllerAnimated(true, null)
                    }
                )
            }

            controller?.let { root.presentViewController(it, animated = true, completion = null) }
        }
    }
}
