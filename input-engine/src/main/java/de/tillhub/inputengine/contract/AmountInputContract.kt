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
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.moneyinput.AmountInputResultStatus
import de.tillhub.inputengine.ui.moneyinput.MoneyInputActivity
import de.tillhub.inputengine.ui.pininput.InputResultStatus
import kotlinx.parcelize.Parcelize
import java.math.BigInteger
import java.util.Currency

@ExperimentalMaterial3Api
class AmountInputContract : ActivityResultContract<AmountResultStatus, AmountInputResultStatus>() {

    override fun createIntent(context: Context, input: AmountResultStatus): Intent {
        return Intent(context, MoneyInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AmountInputResultStatus {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(
                it,
                ExtraKeys.EXTRAS_RESULT,
                AmountInputResultStatus.Success::class.java
            )
        } ?: AmountInputResultStatus.Canceled
    }
}

@Parcelize
data class AmountResultStatus(
    val amount: BigInteger = BigInteger.ZERO,
    val currency: Currency,
    val isZeroAllowed: Boolean = false,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_title),
    val amountMin: MoneyParam = MoneyParam.Disable,
    val amountMax: MoneyParam = MoneyParam.Disable,
    val hintAmount: MoneyParam = MoneyParam.Disable,
    val extra: Bundle = bundleOf()
) : Parcelable