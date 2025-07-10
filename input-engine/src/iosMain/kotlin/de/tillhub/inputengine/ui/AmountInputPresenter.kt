package de.tillhub.inputengine.ui

import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.ui.amount.AmountInputScreen
import de.tillhub.inputengine.ui.amount.AmountInputViewModel
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class AmountInputPresenter(private val onResult: (AmountInputResult) -> Unit) {

    private var viewController: UIViewController? = null

    fun launch(request: AmountInputRequest) {
        val rootVC = UIApplication.Companion.sharedApplication.keyWindow?.rootViewController ?: return

        viewController = ComposeUIViewController {
            AmountInputScreen(
                onResult = {
                    onResult(it)
                    dismiss()
                },
                onDismiss = {
                    onResult(AmountInputResult.Canceled)
                    dismiss()
                },
                viewModel = viewModel(
                    factory = AmountInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(AmountInputViewModel.REQUEST_KEY, request)
                        set(AmountInputViewModel.FORMATTER_KEY, MoneyFormatter())
                    },
                ),
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
