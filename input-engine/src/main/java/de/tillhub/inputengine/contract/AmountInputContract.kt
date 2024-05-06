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
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.moneyinput.MoneyInputActivity
import kotlinx.parcelize.Parcelize
import java.util.Currency
import java.util.Locale

@ExperimentalMaterial3Api
class AmountInputContract : ActivityResultContract<AmountInputRequest, AmountInputResult>() {

    override fun createIntent(context: Context, input: AmountInputRequest): Intent {
        return Intent(context, MoneyInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AmountInputResult {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRAS_RESULT, AmountInputResult.Success::class.java)
        } ?: AmountInputResult.Canceled
    }
}

@Parcelize
data class AmountInputRequest(
    val amount: MoneyIO = MoneyIO.zero(Currency.getInstance(Locale.getDefault())),
    val isZeroAllowed: Boolean = false,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_title_amount),
    val amountMin: MoneyParam = MoneyParam.Disable,
    val amountMax: MoneyParam = MoneyParam.Disable,
    val hintAmount: MoneyParam = MoneyParam.Disable,
    val extras: Bundle = bundleOf()
) : Parcelable

@Parcelize
sealed class AmountInputResult : Parcelable {
    data class Success(
        val amount: MoneyIO,
        val extras: Bundle
    ) : AmountInputResult()

    data object Canceled : AmountInputResult()
}