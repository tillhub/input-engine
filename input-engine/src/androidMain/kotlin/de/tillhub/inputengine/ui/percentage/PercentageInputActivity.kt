package de.tillhub.inputengine.ui.percentage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.json.Json

class PercentageInputActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestJson = intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("Missing PercentageInputRequest")

        val request = Json.decodeFromString<PercentageInputRequest>(requestJson)

        setContent {
            PercentageInputScreen(
                request = request,
                onResult = {
                    val resultIntent = Intent().apply {
                        putExtra(
                            ExtraKeys.EXTRAS_RESULT,
                            it.percent,
                        )
                        putExtra(
                            ExtraKeys.EXTRAS_ARGS,
                            Bundle().apply {
                                it.extras.forEach { (k, v) -> putInt(k, v) }
                            },
                        )
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onDismiss = {
                    setResult(RESULT_CANCELED)
                    finish()
                },
            )
        }
    }
}
