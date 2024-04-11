package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.os.BundleCompat
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.quantity.QuantityInputActivity
import de.tillhub.inputengine.ui.quantity.QuantityInputResultStatus
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@ExperimentalMaterial3Api
class QuantityInputContract : ActivityResultContract<QuantityInputRequest, QuantityInputResultStatus>() {

    override fun createIntent(context: Context, input: QuantityInputRequest): Intent {
        return Intent(context, QuantityInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): QuantityInputResultStatus {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(
                it,
                ExtraKeys.EXTRAS_RESULT,
                QuantityInputResultStatus.Success::class.java
            )
        } ?: QuantityInputResultStatus.Canceled
    }
}

@Parcelize
data class QuantityInputRequest(
    val quantity: BigDecimal,
    val quantityHint: QuantityParam,
    val allowsNegatives: Boolean = true,
    val minQuantity: BigDecimal?,
    val maxQuantity: BigDecimal?,
    val toolbarTitle: StringParam = StringParam.StringResource(
        R.string.numpad_title_quantity
    ),
) : Parcelable