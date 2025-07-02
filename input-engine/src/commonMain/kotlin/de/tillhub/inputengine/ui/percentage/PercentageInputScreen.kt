package de.tillhub.inputengine.ui.percentage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.allStringResources
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.PercentageInputPreview
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PercentageInputScreen(
    onResult: (PercentageInputResult.Success) -> Unit,
    onDismiss: () -> Unit,
    viewModel: PercentageInputViewModel
) {
    val displayData by viewModel.percentageInput.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = stringResource(Res.allStringResources.getValue(viewModel.toolbarTitle)),
                    onClick = onDismiss,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                PercentageInputPreview(
                    percentText = displayData.text,
                    percentageMin = viewModel.minStringParam,
                    percentageMax = viewModel.maxStringParam,
                )
                NumberKeyboard(
                    onClick = viewModel::input,
                    showDecimalSeparator = viewModel.allowDecimal,
                )
                SubmitButton(displayData.isValid) {
                    onResult(
                        PercentageInputResult.Success(
                            percent = displayData.percent,
                            extras = viewModel.responseExtras
                        ),
                    )
                }
            }
        }
    }
}
