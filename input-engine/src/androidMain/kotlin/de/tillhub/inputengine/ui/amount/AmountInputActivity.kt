package de.tillhub.inputengine.ui.amount

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.json.Json

class AmountInputActivity : ComponentActivity() {

    val request: AmountInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)?.let { requestJson ->
            Json.decodeFromString(requestJson)
        } ?: throw IllegalArgumentException("Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmountInputScreen(
                request = request,
                onResult = {
                    val resultIntent = Intent().apply {
                        val extras = Bundle().apply {
                            it.extras.forEach { (key, value) -> putString(key, value) }
                        }
                        val result = Json.encodeToString(MoneyIOSerializer, it.amount)
                        putExtra(ExtraKeys.EXTRAS_RESULT, result)
                        putExtra(ExtraKeys.EXTRAS_ARGS, extras)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onDismiss = {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            )
        }
    }
}