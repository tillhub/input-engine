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
        private var viewController: UIViewController? = null

        override fun launchPinInput(request: PinInputRequest) {
            val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

            viewController = ComposeUIViewController {
                PinInputScreen(
                    request = request,
                    onResult = {
                        onResult(it)
                        viewController?.dismissViewControllerAnimated(true, null)
                    }
                )
            }

            viewController?.let { root.presentViewController(it, animated = true, completion = null) }
        }
    }
}
