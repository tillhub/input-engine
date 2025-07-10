package de.tillhub.inputengine.ui.pininput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.allStringResources
import de.tillhub.inputengine.resources.pin_correct
import de.tillhub.inputengine.resources.pin_wrong
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
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
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = stringResource(Res.allStringResources.getValue(viewModel.toolbarTitle)),
                    onBackClick = { onResult(PinInputResult.Canceled) },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(vertical = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                PinInputPreview(
                    pinText = enteredPin,
                    hintText = viewModel.hint,
                    overridePinInput = viewModel.overridePinInput,
                    onOverride = { onResult(PinInputResult.Success(viewModel.responseExtras)) },
                )
                NumberKeyboard(onClick = viewModel::input)
            }
        }
    }
}
