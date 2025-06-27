package de.tillhub.inputengine.ui.pin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.helper.ExtraKeys
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import kotlinx.serialization.json.Json
import android.os.Bundle as AndroidBundle

class PinInputActivity : ComponentActivity() {

    private lateinit var request: PinInputRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("Missing PinInputRequest")
        request = Json.decodeFromString(json)

        setContent {
            PinInputScreen(
                request = request,
                onResult = { result ->
                    when (result) {
                        is PinInputResult.Success -> {
                            val resultIntent = Intent().apply {
                                putExtra(
                                    ExtraKeys.EXTRAS_ARGS,
                                    AndroidBundle().apply {
                                        result.extras.forEach { (key, value) ->
                                            putString(key, value)
                                        }
                                    },
                                )
                            }
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }

                        PinInputResult.Canceled -> {
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    }
                },
            )
        }
    }
}
