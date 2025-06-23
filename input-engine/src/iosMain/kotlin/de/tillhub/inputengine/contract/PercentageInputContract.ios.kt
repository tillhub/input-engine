package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.ui.percentage.PercentageInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController


@Composable
actual fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit
): PercentageInputContract = remember {
    object : PercentageInputContract {
        private var viewController: UIViewController? = null

        override fun launchPercentageInput(request: PercentageInputRequest) {
            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

            viewController = ComposeUIViewController {
                PercentageInputScreen(
                    request = request,
                    onResult = {
                        onResult(it)
                        dismiss()
                    },
                    onDismiss = {
                        onResult(PercentageInputResult.Canceled)
                        dismiss()
                    }
                )
            }

            rootVC.presentViewController(viewController!!, animated = true, completion = null)
        }

        private fun dismiss() {
            viewController?.dismissViewControllerAnimated(true) {
                viewController = null
            }
        }
    }
}
