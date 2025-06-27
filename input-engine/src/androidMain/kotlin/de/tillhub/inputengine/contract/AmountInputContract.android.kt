package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.amount.AmountInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting

@Composable
actual fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit,
): AmountInputContract {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        onResult(parseAmountInputResult(result.resultCode, result.data?.extras))
    }

    return remember {
        object : AmountInputContract {
            override fun launchAmountInput(request: AmountInputRequest) {
                val intent = Intent(context, AmountInputActivity::class.java).apply {
                    putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}

@VisibleForTesting
internal fun parseAmountInputResult(resultCode: Int, extras: Bundle?): AmountInputResult {
    if (resultCode != Activity.RESULT_OK || extras == null) {
        return AmountInputResult.Canceled
    }
    val amount = extras.getString(ExtraKeys.EXTRAS_RESULT)
        ?.let { Json.decodeFromString(MoneyIOSerializer, it) }
        ?: return AmountInputResult.Canceled

    val extrasMap = extras.getBundle(ExtraKeys.EXTRAS_ARGS)
        ?.let { bundle ->
            bundle.keySet().associateWith { bundle.getInt(it) }
        }.orEmpty()

    return AmountInputResult.Success(amount, extrasMap)
}
