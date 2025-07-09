package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.ui.PercentageInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
                    putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}

private fun parsePercentageInputResult(resultCode: Int, extras: Bundle?): PercentageInputResult {
    if (resultCode != Activity.RESULT_OK || extras == null) {
        return PercentageInputResult.Canceled
    }

    return extras.getString(ExtraKeys.EXTRAS_RESULT)
        ?.let { Json.decodeFromString<PercentageInputResult.Success>(it) }
        ?: PercentageInputResult.Canceled
}
