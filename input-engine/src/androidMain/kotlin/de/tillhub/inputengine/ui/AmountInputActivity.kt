package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.domain.serializer.MoneyIOSerializer
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.domain.ExtraKeys
import de.tillhub.inputengine.ui.amount.AmountInputScreen
import de.tillhub.inputengine.ui.amount.AmountInputViewModel
import kotlinx.serialization.json.Json

class AmountInputActivity : ComponentActivity() {

    private val request: AmountInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)?.let { requestJson ->
            Json.Default.decodeFromString(requestJson)
        } ?: throw IllegalArgumentException("Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmountInputScreen(
                onResult = {
                    val resultIntent = Intent().apply {
                        val extras = Bundle().apply {
                            it.extras.forEach { (key, value) -> putString(key, value) }
                        }
                        val result = Json.Default.encodeToString(MoneyIOSerializer, it.amount)
                        putExtra(ExtraKeys.EXTRAS_RESULT, result)
                        putExtra(ExtraKeys.EXTRAS_ARGS, extras)
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
                )
            )
        }
    }
}