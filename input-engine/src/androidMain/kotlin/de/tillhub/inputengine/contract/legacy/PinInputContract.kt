package de.tillhub.inputengine.contract.legacy

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.contract.parsePinInputResult
import de.tillhub.inputengine.ui.PinInputActivity
import kotlinx.serialization.json.Json

class PinInputContract : ActivityResultContract<PinInputRequest, PinInputResult>() {

    override fun createIntent(context: Context, input: PinInputRequest): Intent = Intent(context, PinInputActivity::class.java).apply {
        putExtra(ExtraKeys.EXTRAS_REQUEST, Json.Default.encodeToString(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PinInputResult = parsePinInputResult(resultCode, intent?.extras)
}
