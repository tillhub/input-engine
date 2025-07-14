package de.tillhub.inputengine.ui

import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.formatting.PercentageFormatterImpl
import de.tillhub.inputengine.ui.screens.PercentageInputScreen
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class PercentageInputPresenter(
    private val onResult: (PercentageInputResult) -> Unit,
) {
    private var viewController: UIViewController? = null

    fun launch(request: PercentageInputRequest) {
        val rootVC =
            UIApplication.Companion.sharedApplication.keyWindow
                ?.rootViewController ?: return

        viewController =
            ComposeUIViewController {
                PercentageInputScreen(
                    onResult = {
                        onResult(it)
                        dismiss()
                    },
                    onDismiss = {
                        onResult(PercentageInputResult.Canceled)
                        dismiss()
                    },
                    viewModel =
                    viewModel(
                        factory = PercentageInputViewModel.Factory,
                        extras =
                        MutableCreationExtras().apply {
                            set(PercentageInputViewModel.REQUEST_KEY, request)
                            set(PercentageInputViewModel.FORMATTER_KEY, PercentageFormatterImpl())
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
