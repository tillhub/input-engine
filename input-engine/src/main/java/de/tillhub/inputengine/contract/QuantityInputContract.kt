package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.quantity.QuantityInputActivity
import kotlinx.parcelize.Parcelize

@ExperimentalMaterial3Api
class QuantityInputContract : ActivityResultContract<QuantityInputRequest, QuantityInputResult>() {

    override fun createIntent(context: Context, input: QuantityInputRequest): Intent {
        return Intent(context, QuantityInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): QuantityInputResult {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRAS_RESULT, QuantityInputResult.Success::class.java)
        } ?: QuantityInputResult.Canceled
    }
}

@Parcelize
class QuantityInputRequest(
    val quantity: QuantityIO,
    val quantityHint: QuantityParam = QuantityParam.Disable,
    val allowsNegatives: Boolean = true,
    val allowsZero: Boolean = false,
    val minQuantity: QuantityParam = QuantityParam.Disable,
    val maxQuantity: QuantityParam = QuantityParam.Disable,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_title_quantity),
    val extras: Bundle = bundleOf()
) : Parcelable

@Parcelize
sealed class QuantityInputResult : Parcelable {
    class Success(val quantity: QuantityIO, val extras: Bundle) : QuantityInputResult()
    data object Canceled : QuantityInputResult()
}