package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.ui.amount.AmountInputScreen
import de.tillhub.inputengine.ui.amount.AmountInputViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AmountInputActivity : ComponentActivity() {

    private val request: AmountInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRAS_REQUEST)?.let { requestJson ->
            Json.decodeFromString(requestJson)
        } ?: throw IllegalArgumentException("Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmountInputScreen(
                onResult = { result ->
                    val resultIntent = Intent().apply {
                        putExtra(ExtraKeys.EXTRAS_RESULT, Json.encodeToString(result))
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onDismiss = {
                    setResult(RESULT_CANCELED)
                    finish()
                },
                viewModel = viewModel(
                    factory = AmountInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(AmountInputViewModel.REQUEST_KEY, request)
                        set(AmountInputViewModel.FORMATTER_KEY, MoneyFormatter())
                    },
                ),
            )
        }
    }
}
