package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.ui.amount.AmountInputRequest
import de.tillhub.inputengine.ui.amount.AmountInputResult
import de.tillhub.inputengine.ui.amount.AmountInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@Composable
actual fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit
): AmountInputContract = remember {
    object : AmountInputContract {
        private var viewController: UIViewController? = null

        override fun launchAmountInput( request: AmountInputRequest) {
            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

            viewController = ComposeUIViewController {
                AmountInputScreen(
                    request = request,
                    onResult = {
                        onResult(it)
                        dismiss()
                    },
                    onDismiss = {
                        onResult(AmountInputResult.Canceled)
                        dismiss()
                    }
                )
            }

            viewController?.let { vc ->
                rootVC.presentViewController(vc, animated = true, completion = null)
            }
        }

        private fun dismiss() {
            viewController?.dismissViewControllerAnimated(true) {
                viewController = null
            }
        }
    }
}
