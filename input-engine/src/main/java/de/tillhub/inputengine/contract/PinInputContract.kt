package de.tillhub.inputengine.contract

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
class PinInputContract : ActivityResultContract<PinInputRequest, Int>() {

    override fun createIntent(context: Context, input: PinInputRequest): Intent {
        return Intent(context, PinInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}

@Parcelize
data class PinInputRequest(
    val pin: String,
    val toolbarTitle: StringParam = StringParam.StringResource(
        R.string.pin_numpad_title
    ),
) : Parcelable