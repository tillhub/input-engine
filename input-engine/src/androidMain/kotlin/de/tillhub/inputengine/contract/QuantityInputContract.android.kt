package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import org.jetbrains.annotations.VisibleForTesting

@Composable
actual fun rememberQuantityInputLauncher(
    onResult: (QuantityInputResult) -> Unit,
): QuantityInputContract {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        onResult(parseQuantityInputResult(result.resultCode, result.data?.extras))
    }

    return remember {
        object : QuantityInputContract {
            override fun launchQuantityInput(request: QuantityInputRequest) {
                val intent = Intent(context, QuantityInputActivity::class.java).also {
                    it.putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}

@VisibleForTesting
internal fun parseQuantityInputResult(
    resultCode: Int,
    extras: Bundle?,
): QuantityInputResult {
    if (resultCode != Activity.RESULT_OK || extras == null) {
        return QuantityInputResult.Canceled
    }

    val quantityValue = extras.getDouble(ExtraKeys.EXTRAS_RESULT, Double.NaN)
    if (quantityValue.isNaN()) return QuantityInputResult.Canceled

    val extrasMap = extras.getBundle(ExtraKeys.EXTRAS_ARGS)
        ?.keySet()
        ?.associateWith { key -> extras.getBundle(ExtraKeys.EXTRAS_ARGS)!!.getInt(key) }
        .orEmpty()

    return QuantityInputResult.Success(
        quantity = QuantityIO.of(quantityValue),
        extras = extrasMap,
    )
}
