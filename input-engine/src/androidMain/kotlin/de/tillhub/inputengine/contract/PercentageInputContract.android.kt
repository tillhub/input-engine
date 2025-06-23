package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
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

@Composable
actual fun rememberPercentageInputLauncher(
    onResult: (PercentageInputResult) -> Unit
): PercentageInputContract {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = result.data?.extras
        val percentValue = resultData?.getDouble(ExtraKeys.EXTRAS_RESULT)
        val extras = resultData
            ?.getBundle(ExtraKeys.EXTRAS_ARGS)
            ?.keySet()
            ?.associateWith { resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(it).orEmpty() }
            .orEmpty()

        val finalResult = when (result.resultCode) {
            Activity.RESULT_OK -> PercentageInputResult.Success(
                PercentIO.of(checkNotNull(percentValue)), extras
            )
            else -> PercentageInputResult.Canceled
        }

        onResult(finalResult)
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
