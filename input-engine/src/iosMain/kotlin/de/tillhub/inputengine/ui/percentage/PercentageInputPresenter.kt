package de.tillhub.inputengine.ui.percentage

import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class PercentageInputPresenter(
    private val onResult: (PercentageInputResult) -> Unit
) {

    private var viewController: UIViewController? = null

    fun launch(request: PercentageInputRequest) {
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
