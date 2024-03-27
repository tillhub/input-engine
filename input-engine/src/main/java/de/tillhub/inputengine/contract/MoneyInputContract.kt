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
import de.tillhub.inputengine.data.AmountParam
import de.tillhub.inputengine.data.Money
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.moneyinput.InputResultStatus
import de.tillhub.inputengine.ui.moneyinput.MoneyInputActivity
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@ExperimentalMaterial3Api
class MoneyInputContract : ActivityResultContract<MoneyInputRequest, InputResultStatus>() {

    override fun createIntent(context: Context, input: MoneyInputRequest): Intent {
        return Intent(context, MoneyInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): InputResultStatus {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(
                it,
                ExtraKeys.EXTRAS_RESULT,
                InputResultStatus.Success::class.java
            )
        } ?: InputResultStatus.Cancel
    }
}

@Parcelize
data class MoneyInputRequest(
    val amount: BigDecimal = Money.zero().value,
    val currency: String,
    val isZeroAllowed: Boolean = false,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_keyboard_title),
    val amountMin: AmountParam = AmountParam.Disable,
    val amountMax: AmountParam = AmountParam.Disable,
    val hintAmount: AmountParam = AmountParam.Disable,
    val extra: Bundle = bundleOf()
) : Parcelable