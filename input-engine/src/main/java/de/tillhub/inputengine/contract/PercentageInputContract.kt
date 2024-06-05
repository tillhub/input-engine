package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.percentage.PercentageInputActivity
import kotlinx.parcelize.Parcelize

class PercentageInputContract :
    ActivityResultContract<PercentageInputRequest, PercentageInputResult>() {

    override fun createIntent(context: Context, input: PercentageInputRequest): Intent {
        return Intent(context, PercentageInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PercentageInputResult {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.extras?.let {
            val percent =
                BundleCompat.getSerializable(it, ExtraKeys.EXTRAS_RESULT, PercentIO::class.java)
            val extras = it.getBundle(ExtraKeys.EXTRAS_ARGS)
            PercentageInputResult.Success(checkNotNull(percent), checkNotNull(extras))
        } ?: PercentageInputResult.Canceled
    }
}

@Parcelize
class PercentageInputRequest(
    val percent: PercentIO = PercentIO.ZERO,
    val toolbarTitle: StringParam = StringParam.StringResource(R.string.numpad_title_percentage),
    val percentageMin: PercentageParam = PercentageParam.Disable,
    val percentageMax: PercentageParam = PercentageParam.Disable,
    val allowsZero: Boolean = true,
    val allowDecimal: Boolean = true,
    val extras: Bundle = bundleOf()
) : Parcelable

sealed class PercentageInputResult {
    class Success(val percent: PercentIO, val extras: Bundle) : PercentageInputResult()
    data object Canceled : PercentageInputResult()
}