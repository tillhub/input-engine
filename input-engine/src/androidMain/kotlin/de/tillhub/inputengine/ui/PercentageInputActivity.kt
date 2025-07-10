package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.formatting.PercentageFormatter
import de.tillhub.inputengine.formatting.PercentageFormatterImpl
import de.tillhub.inputengine.ui.percentage.PercentageInputScreen
import de.tillhub.inputengine.ui.percentage.PercentageInputViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PercentageInputActivity : ComponentActivity() {

    private val request: PercentageInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRAS_REQUEST)?.let { requestJson ->
            Json.Default.decodeFromString<PercentageInputRequest>(requestJson)
        } ?: throw IllegalArgumentException("Argument PercentageInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PercentageInputScreen(
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
                    factory = PercentageInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(PercentageInputViewModel.REQUEST_KEY, request)
                        set(PercentageInputViewModel.FORMATTER_KEY, PercentageFormatterImpl())
                    },
                ),
            )
        }
    }
}
