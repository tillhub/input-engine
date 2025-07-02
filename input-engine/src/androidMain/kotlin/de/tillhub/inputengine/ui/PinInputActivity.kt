package de.tillhub.inputengine.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.domain.ExtraKeys
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import de.tillhub.inputengine.ui.pininput.PinInputViewModel
import kotlinx.serialization.json.Json

class PinInputActivity : ComponentActivity() {

    private lateinit var request: PinInputRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("Missing PinInputRequest")
        request = Json.Default.decodeFromString(json)

        setContent {
            PinInputScreen(
                onResult = { result ->
                    when (result) {
                        is PinInputResult.Success -> {
                            val resultIntent = Intent().apply {
                                putExtra(
                                    ExtraKeys.EXTRAS_ARGS,
                                    Bundle().apply {
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
                viewModel = viewModel(
                    factory = PinInputViewModel.Factory,
                    extras = MutableCreationExtras().apply {
                        set(PinInputViewModel.REQUEST_KEY, request)
                    },
                )
            )
        }
    }
}