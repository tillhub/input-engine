package de.tillhub.inputengine.ui.quantity

import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class QuantityInputPresenter(
    private val onResult: (QuantityInputResult) -> Unit
) {

    private var viewController: UIViewController? = null

    fun launch(request: QuantityInputRequest) {
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
