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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.formatter.PercentageFormatter
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_percentage
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Composable
fun PercentageInputScreen(
    request: PercentageInputRequest,
    viewModel: PercentageInputViewModel = remember { PercentageInputViewModel() },
    onResult: (PercentageInputResult.Success) -> Unit,
    onDismiss: () -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.init(request)
    }

    val title = stringResource(Res.string.numpad_title_percentage)
    val displayData by viewModel.percentageInput.collectAsStateWithLifecycle()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier
            ),
            topBar = { Toolbar(title) { onDismiss() } }
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
                Numpad(
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
    percentText: String = "",
    percentageMin: PercentageParam = PercentageParam.Disable,
    percentageMax: PercentageParam = PercentageParam.Disable
) {
    if (percentageMax is PercentageParam.Enable) {
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = "max. ${PercentageFormatter.format(percentageMax.percent)}",
            color = MagneticGrey,
        )
    }

    Text(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
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
            text = "min. ${PercentageFormatter.format(percentageMin.percent)}",
            color = MagneticGrey,
        )
    }
}

@Serializable
data class PercentageInputRequest(
    val percent: PercentIO = PercentIO.ZERO,
    val allowsZero: Boolean = false,
    val allowDecimal: Boolean = false,
    val percentageMin: PercentageParam = PercentageParam.Disable,
    val percentageMax: PercentageParam = PercentageParam.Disable,
    val extras: Map<String, String> = emptyMap()
)

@Serializable
sealed class PercentageInputResult {
    @Serializable
    data class Success(
        val percent: PercentIO,
        val extras: Map<String, String> = emptyMap()
    ) : PercentageInputResult()

    @Serializable
    data object Canceled : PercentageInputResult()
}
