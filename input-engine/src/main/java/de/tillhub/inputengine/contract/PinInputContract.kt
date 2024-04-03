package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.ui.pininput.PinInputActivity
import kotlinx.parcelize.Parcelize

@ExperimentalMaterial3Api
class PinInputContract : ActivityResultContract<PinInputRequest, PinInputResult>() {

    override fun createIntent(context: Context, input: PinInputRequest): Intent {
        return Intent(context, PinInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PinInputResult {
        return when (resultCode) {
            Activity.RESULT_OK -> PinInputResult.Success
            else -> PinInputResult.Canceled
        }
    }
}

@Parcelize
data class PinInputRequest(
    val pin: String,
    val overridePinInput: Boolean = false,
    val toolbarTitle: StringParam = StringParam.StringResource(
        R.string.pin_numpad_title
    ),
) : Parcelable

sealed class PinInputResult {
    data object Success : PinInputResult()
    data object Canceled : PinInputResult()
}