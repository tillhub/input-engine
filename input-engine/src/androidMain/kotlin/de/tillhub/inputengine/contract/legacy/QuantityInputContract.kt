package de.tillhub.inputengine.contract.legacy

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.contract.parseQuantityInputResult
import de.tillhub.inputengine.ui.QuantityInputActivity
import kotlinx.serialization.json.Json

class QuantityInputContract : ActivityResultContract<QuantityInputRequest, QuantityInputResult>() {

    override fun createIntent(context: Context, input: QuantityInputRequest): Intent = Intent(context, QuantityInputActivity::class.java).apply {
        putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): QuantityInputResult = parseQuantityInputResult(resultCode, intent?.extras)
}
