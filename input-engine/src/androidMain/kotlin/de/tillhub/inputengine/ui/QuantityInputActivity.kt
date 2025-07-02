package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.formatting.QuantityFormatter
import de.tillhub.inputengine.domain.ExtraKeys
import de.tillhub.inputengine.ui.quantity.QuantityInputScreen
import de.tillhub.inputengine.ui.quantity.QuantityInputViewModel
import kotlinx.serialization.json.Json

class QuantityInputActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("Missing QuantityInputRequest")
        val request = Json.Default.decodeFromString<QuantityInputRequest>(json)

        setContent {
            QuantityInputScreen(
                onResult = {
                    when (it) {
                        is QuantityInputResult.Success -> {
                            val intent = Intent().apply {
                                putExtra(ExtraKeys.EXTRAS_RESULT, it.quantity.toDouble())
                                putExtra(
                                    ExtraKeys.EXTRAS_ARGS,
                                    Bundle().apply {
                                        it.extras.forEach { (k, v) -> putString(k, v) }
                                    },
                                )
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
                        set(QuantityInputViewModel.FORMATTER_KEY, QuantityFormatter())
                    },
                )
            )
        }
    }
}