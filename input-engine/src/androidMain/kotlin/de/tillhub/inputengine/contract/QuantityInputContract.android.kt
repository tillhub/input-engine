package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.ui.QuantityInputActivity
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
                    it.putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
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

    return extras.getString(ExtraKeys.EXTRAS_RESULT)
        ?.let { Json.decodeFromString<QuantityInputResult.Success>(it) }
        ?: QuantityInputResult.Canceled
}
