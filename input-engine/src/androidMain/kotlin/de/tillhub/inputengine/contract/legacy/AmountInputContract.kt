package de.tillhub.inputengine.contract.legacy

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.parseAmountInputResult
import de.tillhub.inputengine.ui.AmountInputActivity
import kotlinx.serialization.json.Json

class AmountInputContract : ActivityResultContract<AmountInputRequest, AmountInputResult>() {
    override fun createIntent(context: Context, input: AmountInputRequest): Intent = Intent(context, AmountInputActivity::class.java).apply {
        putExtra(ExtraKeys.EXTRAS_REQUEST, Json.Default.encodeToString(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): AmountInputResult = parseAmountInputResult(
        resultCode = resultCode,
        extras = intent?.extras,
    )
}
