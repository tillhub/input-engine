package de.tillhub.inputengine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_pin
import de.tillhub.inputengine.resources.pin_correct
import de.tillhub.inputengine.resources.pin_wrong
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
import de.tillhub.inputengine.ui.PinInputState
import de.tillhub.inputengine.ui.PinInputViewModel
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.PinInputPreview
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PinInputScreen(
    onResult: (PinInputResult) -> Unit,
    viewModel: PinInputViewModel,
) {
    val errorMessage = stringResource(Res.string.pin_wrong)
    val correctPin = stringResource(Res.string.pin_correct)

    val title = when (val title = viewModel.toolbarTitle) {
        StringParam.Disable -> stringResource(Res.string.numpad_title_pin)
        is StringParam.Enable -> title.value
    }
    val enteredPin by viewModel.enteredPin.collectAsState()
    val pinInputState by viewModel.pinInputState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
            onResult(PinInputResult.Success(viewModel.responseExtras))
        }

        PinInputState.InvalidPinFormat -> {
            onResult(PinInputResult.Canceled)
        }

        else -> Unit
    }

    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Scaffold(
                modifier =
                getModifierBasedOnDeviceType(
                    isTablet = TabletScaffoldModifier,
                    isMobile = Modifier,
                ),
                topBar = {
                    Toolbar(
                        title = title,
                        onBackClick = { onResult(PinInputResult.Canceled) },
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { innerPadding ->
                Column(
                    modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    PinInputPreview(
                        modifier = Modifier.weight(1f),
                        pinText = enteredPin,
                        hintText = viewModel.hint,
                        overridePinInput = viewModel.overridePinInput,
                        onOverride = { onResult(PinInputResult.Success(viewModel.responseExtras)) },
                    )
                    NumberKeyboard(
                        modifier = Modifier.padding(vertical = 24.dp),
                        onClick = viewModel::input,
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}
