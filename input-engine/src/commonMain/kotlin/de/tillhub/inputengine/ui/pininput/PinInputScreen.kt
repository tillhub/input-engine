package de.tillhub.inputengine.ui.pininput


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_pin
import de.tillhub.inputengine.resources.pin_correct
import de.tillhub.inputengine.resources.pin_enter
import de.tillhub.inputengine.resources.pin_wrong
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.HintGray
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import de.tillhub.inputengine.ui.theme.textFieldTransparentColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun PinInputScreen(
    request: PinInputRequest,
    viewModel: PinInputViewModel = remember { PinInputViewModel() },
    onResult: (PinInputResult) -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.init(request.pin)
    }

    val errorMessage = stringResource(Res.string.pin_wrong)
    val correctPin = stringResource(Res.string.pin_correct)
    val enteredPin by viewModel.enteredPin.collectAsState()
    val pinInputState by viewModel.pinInputState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pinInputState) {
        when (pinInputState) {
            PinInputState.PinInvalid -> {
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
                viewModel.input(NumpadKey.Clear)
            }

            PinInputState.PinValid -> {
                scope.launch {
                    snackbarHostState.showSnackbar(correctPin)
                }
                onResult(PinInputResult.Success(request.extras))
            }

            PinInputState.InvalidPinFormat -> {
                onResult(PinInputResult.Canceled)
            }

            else -> Unit
        }
    }

    val title = stringResource(Res.string.numpad_title_pin)

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier
            ),
            topBar = { Toolbar(title = title) { onResult(PinInputResult.Canceled) } },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(vertical = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PinPreview(
                    pin = enteredPin,
                    overridePinInput = request.overridePinInput,
                    hint = "•".repeat(request.pin.length),
                    onOverride = { onResult(PinInputResult.Success(request.extras)) }
                )
                Numpad(viewModel::input)
            }
        }
    }
}

@Composable
private fun PinPreview(
    pin: String,
    overridePinInput: Boolean,
    hint: String,
    onOverride: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            readOnly = true,
            modifier = Modifier.align(Alignment.Center),
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
                        .fillMaxWidth(),
                    style = TextStyle.Default.copy(
                        color = HintGray,
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center
                    ),
                    text = hint,
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldTransparentColors(),
        )
        if (overridePinInput) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clickable {
                        onOverride()
                    },
                textAlign = TextAlign.End,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(Res.string.pin_enter),
                color = OrbitalBlue
            )
        }
    }
}