package de.tillhub.inputengine.contract

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.quantity.QuantityInputActivity
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@ExperimentalMaterial3Api
class QuantityInputContract : ActivityResultContract<QuantityInputRequest, Int>() {

    override fun createIntent(context: Context, input: QuantityInputRequest): Intent {
        return Intent(context, QuantityInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}

@Parcelize
data class QuantityInputRequest(
    val quantity: BigDecimal,
    val quantityHint: QuantityParam,
    val allowsNegatives: Boolean = true,
    val maxQuantity: BigDecimal,
    val toolbarTitle: StringParam = StringParam.StringResource(
        R.string.numpad_title_quantity
    ),
) : Parcelable