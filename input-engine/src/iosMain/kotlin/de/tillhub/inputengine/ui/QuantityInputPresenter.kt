package de.tillhub.inputengine.ui

import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.formatting.QuantityFormatter
import de.tillhub.inputengine.ui.quantity.QuantityInputScreen
import de.tillhub.inputengine.ui.quantity.QuantityInputViewModel
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class QuantityInputPresenter(
    private val onResult: (QuantityInputResult) -> Unit,
) {

    private var viewController: UIViewController? = null

    fun launch(request: QuantityInputRequest) {
        val rootVC = UIApplication.Companion.sharedApplication.keyWindow?.rootViewController ?: return

        viewController = ComposeUIViewController {
            QuantityInputScreen(
                onResult = {
                    onResult(it)
                    dismiss()
                },
                viewModel = viewModel(
                    factory = QuantityInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(QuantityInputViewModel.REQUEST_KEY, request)
                        set(QuantityInputViewModel.FORMATTER_KEY, QuantityFormatter())
                    }
                )
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