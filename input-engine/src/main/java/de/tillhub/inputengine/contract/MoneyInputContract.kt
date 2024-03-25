package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
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
sealed class MoneyInputRequest : Parcelable {
    abstract val amount: BigDecimal
    abstract val currency: String
    abstract val isZeroAllowed: Boolean
    abstract val amountMin: BigDecimal?
    abstract val amountMax: BigDecimal?
    abstract val hintAmount: BigDecimal?
    abstract val extra: Bundle?

    data class WithTitleStringRes(
        override val amount: BigDecimal,
        override val currency: String,
        override val isZeroAllowed: Boolean,
        override val amountMin: BigDecimal?,
        override val amountMax: BigDecimal?,
        override val hintAmount: BigDecimal? = null,
        override val extra: Bundle? = null,
        @StringRes val toolbarTitle: Int
    ) : MoneyInputRequest()

    data class WithTitleString(
        override val amount: BigDecimal,
        override val currency: String,
        override val isZeroAllowed: Boolean,
        override val amountMin: BigDecimal?,
        override val amountMax: BigDecimal?,
        override val hintAmount: BigDecimal?,
        override val extra: Bundle?,
        val toolbarTitle: String = ""
    ) : MoneyInputRequest()
}
