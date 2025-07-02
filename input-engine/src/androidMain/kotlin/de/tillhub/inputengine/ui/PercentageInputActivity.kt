package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.formatting.PercentageFormatter
import de.tillhub.inputengine.domain.ExtraKeys
import de.tillhub.inputengine.ui.percentage.PercentageInputScreen
import de.tillhub.inputengine.ui.percentage.PercentageInputViewModel
import kotlinx.serialization.json.Json

class PercentageInputActivity : ComponentActivity() {

    private val request: PercentageInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)?.let { requestJson ->
            Json.Default.decodeFromString<PercentageInputRequest>(requestJson)
        } ?: throw IllegalArgumentException("Argument PercentageInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PercentageInputScreen(
                onResult = {
                    val resultIntent = Intent().apply {
                        putExtra(
                            ExtraKeys.EXTRAS_RESULT,
                            it.percent,
                        )
                        putExtra(
                            ExtraKeys.EXTRAS_ARGS,
                            Bundle().apply {
                                it.extras.forEach { (k, v) -> putString(k, v) }
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
                viewModel = viewModel(
                    factory = PercentageInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(PercentageInputViewModel.REQUEST_KEY, request)
                        set(PercentageInputViewModel.FORMATTER_KEY, PercentageFormatter())
                    },
                )
            )
        }
    }
}