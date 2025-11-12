package de.tillhub.inputengine.contract.legacy

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.contract.parsePercentageInputResult
import de.tillhub.inputengine.ui.PercentageInputActivity
import kotlinx.serialization.json.Json

class PercentageInputContract : ActivityResultContract<PercentageInputRequest, PercentageInputResult>() {

    override fun createIntent(context: Context, input: PercentageInputRequest): Intent = Intent(context, PercentageInputActivity::class.java).apply {
        putExtra(ExtraKeys.EXTRAS_REQUEST, Json.Default.encodeToString(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PercentageInputResult = parsePercentageInputResult(resultCode, intent?.extras)
}
