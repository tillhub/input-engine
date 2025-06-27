package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.percentage.PercentageInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting

@Composable
actual fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit,
): PercentageInputContract {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        onResult(parsePercentageInputResult(result.resultCode, result.data?.extras))
    }

    return remember {
        object : PercentageInputContract {
            override fun launchPercentageInput(request: PercentageInputRequest) {
                val intent = Intent(context, PercentageInputActivity::class.java).apply {
                    putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}

@VisibleForTesting
internal fun parsePercentageInputResult(
    resultCode: Int,
    extras: Bundle?,
): PercentageInputResult {
    if (resultCode != Activity.RESULT_OK || extras == null) {
        return PercentageInputResult.Canceled
    }

    val percentValue = extras.getDouble(ExtraKeys.EXTRAS_RESULT, Double.NaN)
    if (percentValue.isNaN()) return PercentageInputResult.Canceled

    val extrasMap = extras.getBundle(ExtraKeys.EXTRAS_ARGS)
        ?.keySet()
        ?.associateWith { key -> extras.getBundle(ExtraKeys.EXTRAS_ARGS)!!.getInt(key) }
        .orEmpty()

    return PercentageInputResult.Success(
        percent = PercentIO.of(percentValue),
        extras = extrasMap,
    )
}
