package de.tillhub.inputengine.ui.amount

import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class AmountInputPresenter(private val onResult: (AmountInputResult) -> Unit) {

    private var viewController: UIViewController? = null

    fun launch(request: AmountInputRequest) {
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
                },
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
