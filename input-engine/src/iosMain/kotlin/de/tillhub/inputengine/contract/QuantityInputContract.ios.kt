package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.ui.quantity.QuantityInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@Composable
actual fun rememberQuantityInputLauncher(
    onResult: (QuantityInputResult) -> Unit
): QuantityInputContract = remember {
    object : QuantityInputContract {
        private var viewController: UIViewController? = null

        override fun launchQuantityInput(request: QuantityInputRequest) {
            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

            viewController = ComposeUIViewController {
                QuantityInputScreen(
                    request = request,
                    onResult = {
                        onResult(it)
                        dismiss()
                    }
                )
            }
            viewController?.let { rootVC.presentViewController(it, animated = true, completion = null) }
        }

        private fun dismiss() {
            viewController?.dismissViewControllerAnimated(true) {
                viewController = null
            }
        }
    }
}
