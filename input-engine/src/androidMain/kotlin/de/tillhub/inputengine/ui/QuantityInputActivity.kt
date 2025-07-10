package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.formatting.QuantityFormatter
import de.tillhub.inputengine.formatting.QuantityFormatterImpl
import de.tillhub.inputengine.ui.quantity.QuantityInputScreen
import de.tillhub.inputengine.ui.quantity.QuantityInputViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QuantityInputActivity : ComponentActivity() {

    private val request: QuantityInputRequest by lazy {
        intent.getStringExtra(ExtraKeys.EXTRAS_REQUEST)?.let { requestJson ->
            Json.decodeFromString(requestJson)
        } ?: throw IllegalArgumentException("Missing QuantityInputRequest")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuantityInputScreen(
                onResult = { result ->
                    when (result) {
                        is QuantityInputResult.Success -> {
                            val intent = Intent().apply {
                                putExtra(ExtraKeys.EXTRAS_RESULT, Json.encodeToString(result))
                            }
                            setResult(RESULT_OK, intent)
                        }

                        QuantityInputResult.Canceled -> setResult(RESULT_CANCELED)
                    }
                    finish()
                },
                viewModel = viewModel(
                    factory = QuantityInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(QuantityInputViewModel.REQUEST_KEY, request)
                        set(QuantityInputViewModel.FORMATTER_KEY, QuantityFormatterImpl())
                    },
                ),
            )
        }
    }
}
