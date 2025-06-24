package de.tillhub.inputengine.ui.pin

import androidx.compose.ui.window.ComposeUIViewController
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class PinInputPresenter(
    private val onResult: (PinInputResult) -> Unit
) {

    private var viewController: UIViewController? = null

    fun launch(request: PinInputRequest) {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return

        viewController = ComposeUIViewController {
            PinInputScreen(
                request = request,
                onResult = {
                    onResult(it)
                    dismiss()
                }
            )
        }

        viewController?.let { vc ->
            root.presentViewController(vc, animated = true, completion = null)
        }
    }

    private fun dismiss() {
        viewController?.dismissViewControllerAnimated(true) {
            viewController = null
        }
    }
}
