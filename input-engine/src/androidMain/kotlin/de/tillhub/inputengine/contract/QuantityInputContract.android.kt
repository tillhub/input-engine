package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.quantity.QuantityInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
actual fun rememberQuantityInputLauncher(
    onResult: (QuantityInputResult) -> Unit
): QuantityInputContract {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = result.data?.extras
        val extrasMap = resultData
            ?.getBundle(ExtraKeys.EXTRAS_ARGS)
            ?.keySet()
            ?.associateWith { key ->
                resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key).orEmpty()
            }
            .orEmpty()

        val quantity = resultData?.getDouble(ExtraKeys.EXTRAS_RESULT)

        val resultObj = when {
            result.resultCode == Activity.RESULT_OK && quantity != null ->
                QuantityInputResult.Success(
                    quantity = QuantityIO.of(quantity),
                    extras = extrasMap
                )

            else -> QuantityInputResult.Canceled
        }

        onResult(resultObj)
    }

    return remember {
        object : QuantityInputContract {
            override fun launchQuantityInput(request: QuantityInputRequest) {
                val intent = Intent(context, QuantityInputActivity::class.java).apply {
                    putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}
