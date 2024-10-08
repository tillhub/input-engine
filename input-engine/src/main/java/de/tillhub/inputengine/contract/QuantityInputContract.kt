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
            val qty = BundleCompat.getSerializable(it, ExtraKeys.EXTRAS_RESULT, QuantityIO::class.java)
            val extras = it.getBundle(ExtraKeys.EXTRAS_ARGS)
            QuantityInputResult.Success(checkNotNull(qty), checkNotNull(extras))
        } ?: QuantityInputResult.Canceled
    }
}

@Parcelize
class QuantityInputRequest(
    val quantity: QuantityIO = QuantityIO.ZERO,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_title_quantity),
    val quantityHint: QuantityParam = QuantityParam.Disable,
    val minQuantity: QuantityParam = QuantityParam.Disable,
    val maxQuantity: QuantityParam = QuantityParam.Disable,
    val allowsZero: Boolean = false,
    val allowDecimal: Boolean = true,
    val extras: Bundle = bundleOf()
) : Parcelable

sealed class QuantityInputResult {
    class Success(val quantity: QuantityIO, val extras: Bundle) : QuantityInputResult()
    data object Canceled : QuantityInputResult()
}