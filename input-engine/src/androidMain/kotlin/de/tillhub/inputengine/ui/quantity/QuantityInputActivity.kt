package de.tillhub.inputengine.ui.quantity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.json.Json

class QuantityInputActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("Missing QuantityInputRequest")
        val request = Json.decodeFromString<QuantityInputRequest>(json)

        setContent {
            QuantityInputScreen(
                request = request,
                onResult = {
                    when (it) {
                        is QuantityInputResult.Success -> {
                            val intent = Intent().apply {
                                putExtra(ExtraKeys.EXTRAS_RESULT, it.quantity.toDouble())
                                putExtra(
                                    ExtraKeys.EXTRAS_ARGS,
                                    Bundle().apply {
                                        it.extras.forEach { (k, v) -> putInt(k, v) }
                                    }
                                )
                            }
                            setResult(RESULT_OK, intent)
                        }

                        QuantityInputResult.Canceled -> setResult(RESULT_CANCELED)
                    }
                    finish()
                }
            )
        }
    }
}
