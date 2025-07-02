package de.tillhub.inputengine.ui

import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import de.tillhub.inputengine.ui.pininput.PinInputViewModel
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class PinInputPresenter(
    private val onResult: (PinInputResult) -> Unit,
) {

    private var viewController: UIViewController? = null

    fun launch(request: PinInputRequest) {
        val root = UIApplication.Companion.sharedApplication.keyWindow?.rootViewController ?: return

        viewController = ComposeUIViewController {
            PinInputScreen(
                onResult = {
                    onResult(it)
                    dismiss()
                },
                viewModel = viewModel(
                    factory = PinInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(PinInputViewModel.REQUEST_KEY, request)
                    }
                )
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