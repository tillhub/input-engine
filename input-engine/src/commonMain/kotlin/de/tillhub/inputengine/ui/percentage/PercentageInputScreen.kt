package de.tillhub.inputengine.ui.percentage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.formatter.PercentageFormatter
import de.tillhub.inputengine.helper.rememberViewModel
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import de.tillhub.inputengine.resources.numpad_title_percentage
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PercentageInputScreen(
    request: PercentageInputRequest,
    onResult: (PercentageInputResult.Success) -> Unit,
    onDismiss: () -> Unit,
    viewModel: PercentageInputViewModel = rememberViewModel {
        providePercentageInputViewModelFactory(request)
    },
) {
    val displayData by viewModel.percentageInput.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier
            ),
            topBar = { Toolbar(stringResource(Res.string.numpad_title_percentage)) { onDismiss() } }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                InputPreview(
                    percentText = displayData.text,
                    percentageMin = request.percentageMin,
                    percentageMax = request.percentageMax
                )
                NumberKeyboard(
                    onClick = viewModel::input,
                    showDecimalSeparator = request.allowDecimal
                )
                SubmitButton(displayData.isValid) {
                    onResult(
                        PercentageInputResult.Success(displayData.percent, request.extras)
                    )
                }
            }
        }
    }
}

@Composable
fun InputPreview(
    percentText: String,
    percentageMin: PercentageParam = PercentageParam.Disable,
    percentageMax: PercentageParam = PercentageParam.Disable
) {
    if (percentageMax is PercentageParam.Enable) {
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(
                Res.string.max_value,
                PercentageFormatter.format(percentageMax.percent)
            ),
            color = MagneticGrey,
        )
    }

    Text(
        modifier = Modifier.fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .testTag("percentValue"),
        style = MaterialTheme.typography.displaySmall,
        maxLines = 1,
        text = percentText,
        color = OrbitalBlue,
    )

    if (percentageMin is PercentageParam.Enable) {
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(
                Res.string.min_value,
                PercentageFormatter.format(percentageMin.percent)
            ),
            color = MagneticGrey,
        )
    }
}
