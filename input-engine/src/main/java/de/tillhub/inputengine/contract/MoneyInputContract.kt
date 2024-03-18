package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.os.BundleCompat
import de.tillhub.inputengine.ui.MoneyInputActivity
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@ExperimentalMaterial3Api
class MoneyInputContract : ActivityResultContract<MoneyInputRequest, BigDecimal>() {

    override fun createIntent(context: Context, input: MoneyInputRequest): Intent {
        return Intent(context, MoneyInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BigDecimal {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRAS_RESULT, BigDecimal::class.java)
        } ?: BigDecimal.ZERO
    }
}

@Parcelize
data class MoneyInputRequest(
    val title: String,
    val money: BigDecimal,
    val currency: String,
    val isZeroAllowed: Boolean,
    val toolbarTitle: String? = null,
    val targetPriceText: String? = null,
    val submitButtonText: String? = null,
    val requestCode: String = "",
    val amountMin: BigDecimal? = null,
    val amountMax: BigDecimal? = null,
    val amountPrevious: BigDecimal? = null,
) : Parcelable