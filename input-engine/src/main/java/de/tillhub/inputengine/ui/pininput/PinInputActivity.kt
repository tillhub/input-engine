package de.tillhub.inputengine.ui.pininput

import AppTheme
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.theme.HintGray
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TextFieldTransparentColors
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class PinInputActivity : ComponentActivity() {

    private val viewModel by viewModels<PinInputViewModel>()

    private val request: PinInputRequest by lazy {
        intent.extras?.parcelable<PinInputRequest>(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("$TAG: Argument PinInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(request.pin)
        setContent { PinInputScreen() }
    }

    @Composable
    fun PinInputScreen() {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val errorMessage = stringResource(R.string.pin_wrong)
        val correctPin = stringResource(R.string.pin_correct)
        val title = when (val stringParam = request.toolbarTitle) {
            is StringParam.String -> stringParam.value
            is StringParam.StringResource -> stringResource(id = stringParam.resIdRes)
        }
        val enteredPin by viewModel.enteredPin.collectAsStateWithLifecycle()
        val pinInputState by viewModel.pinInputState.collectAsStateWithLifecycle()

        LaunchedEffect(pinInputState) {
            when (pinInputState) {
                PinInputState.AwaitingInput -> Unit
                PinInputState.PinTooLong -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                    viewModel.input(NumpadKey.Clear)
                }

                PinInputState.PinValid -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(correctPin)
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

        AppTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { Snackbar(it) }
                    )
                },
                topBar = {
                    Toolbar(title) {
                        finish()
                    }
                }
            ) {
                PinNumpad(
                    padding = it,
                    pin = enteredPin,
                    overridePinInput = request.overridePinInput,
                    hint = PIN_HINT_CHARACTER.repeat(request.pin.length),
                    onClick = viewModel::input
                )
            }
        }
    }
    @Suppress("LongMethod")
    @ExperimentalMaterial3Api
    @Composable
    fun PinNumpad(
        padding: PaddingValues,
        pin: String,
        overridePinInput: Boolean,
        hint: String,
        onClick: (NumpadKey) -> Unit
    ) {
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 32.dp),
                    value = pin,
                    onValueChange = { },
                    textStyle = TextStyle.Default.copy(
                        color = OrbitalBlue,
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),

                            style = TextStyle.Default.copy(
                                color = HintGray,
                                fontSize = 64.sp,
                                textAlign = TextAlign.Center
                            ),
                            text = hint
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldTransparentColors(),
                )
                if (overridePinInput) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setResult(Activity.RESULT_OK)
                                finish()
                            },
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                        text = stringResource(R.string.pin_enter),
                        color = OrbitalBlue
                    )
                }
                Spacer(modifier = Modifier.height(64.dp))
                Numpad(onClick)
            }
        }
    }

    companion object {
        private const val TAG = "PinInputActivity"
        private const val PIN_HINT_CHARACTER: String = "•"
    }
}
