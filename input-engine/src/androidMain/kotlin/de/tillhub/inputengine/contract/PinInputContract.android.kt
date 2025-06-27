package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.pin.PinInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting

@Composable
actual fun rememberPinInputLauncher(
    onResult: (PinInputResult) -> Unit
): PinInputContract {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onResult(parsePinInputResult(result.resultCode, result.data?.extras))
    }

    return remember {
        object : PinInputContract {
            override fun launchPinInput(request: PinInputRequest) {
                val intent = Intent(context, PinInputActivity::class.java).also {
                    it.putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}

@VisibleForTesting
internal fun parsePinInputResult(resultCode: Int, extras: Bundle?): PinInputResult {
    if (resultCode != Activity.RESULT_OK || extras == null) {
        return PinInputResult.Canceled
    }

    val dataMap = extras.keySet().associateWith { key ->
        extras.getString(key).orEmpty()
    }

    return PinInputResult.Success(dataMap)
}

