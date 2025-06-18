package de.tillhub.inputengine.ui.contract

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.amountinput.AmountInputRequest
import de.tillhub.inputengine.ui.amountinput.AmountInputResult
import de.tillhub.inputengine.ui.amountinput.AmountInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
actual fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit
): AmountInputContract {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = result.data?.extras
        val amountInputResult = when (result.resultCode) {
            Activity.RESULT_OK -> {
                val amountJson = resultData?.getString(ExtraKeys.EXTRAS_RESULT)
                val amount = amountJson?.let { Json.decodeFromString(MoneyIOSerializer, it) }

                val extrasMap = resultData
                    ?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key ->
                        resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key)
                            ?: throw IllegalArgumentException("Non-string value for key: $key")
                    }.orEmpty()

                AmountInputResult.Success(checkNotNull(amount), extrasMap)
            }

            else -> AmountInputResult.Canceled
        }

        onResult(amountInputResult)
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

