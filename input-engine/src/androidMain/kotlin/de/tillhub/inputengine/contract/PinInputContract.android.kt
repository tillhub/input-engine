package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.pin.PinInputActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
actual fun rememberPinInputLauncher(
    onResult: (PinInputResult) -> Unit
): PinInputContract {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val extras = result.data?.extras
        val resultObj = if (result.resultCode == Activity.RESULT_OK && extras != null) {
            val map = extras.keySet().associateWith { extras.getString(it).orEmpty() }
            PinInputResult.Success(map)
        } else PinInputResult.Canceled
        onResult(resultObj)
    }

    return remember {
        object : PinInputContract {
            override fun launchPinInput(request: PinInputRequest) {
                val intent = Intent(context, PinInputActivity::class.java).apply {
                    putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
                }
                launcher.launch(intent)
            }
        }
    }
}
