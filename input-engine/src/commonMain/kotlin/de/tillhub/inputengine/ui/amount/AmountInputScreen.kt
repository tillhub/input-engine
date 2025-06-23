package de.tillhub.inputengine.ui.amount

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.financial.param.MoneyParam
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_amount
import de.tillhub.inputengine.ui.amount.MoneyInputData.Companion.EMPTY
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AmountInputScreen(
    request: AmountInputRequest,
    viewModel: AmountInputViewModel = remember { AmountInputViewModel() },
    onResult: (AmountInputResult.Success) -> Unit,
    onDismiss: () -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.init(request)
    }

    val title = stringResource(Res.string.numpad_title_amount)
    val amountMin by viewModel.uiMinValue.collectAsState()
    val amountMax by viewModel.uiMaxValue.collectAsState()
    val amount by viewModel.moneyInput.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier
            ),
            topBar = {
                Toolbar(title) { onDismiss() }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                InputPreview(
                    amount = amount,
                    amountMin = amountMin,
                    amountMax = amountMax,
                    amountHint = request.hintAmount
                )
                Numpad(
                    onClick = viewModel::input,
                    showNegative = viewModel.amountInputMode == AmountInputMode.BOTH
                )
                SubmitButton(amount.isValid) {
                    onResult(
                        AmountInputResult.Success(
                            amount.money,
                            request.extras
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun InputPreview(
    amount: MoneyInputData = EMPTY,
    amountMin: MoneyParam = MoneyParam.Disable,
    amountMax: MoneyParam = MoneyParam.Disable,
    amountHint: MoneyParam = MoneyParam.Disable
) {
    val (amountString, amountColor) = if (amountHint is MoneyParam.Enable && amount.money.isZero()) {
        MoneyFormatter.format(amountHint.amount) to MagneticGrey
    } else {
        amount.text to OrbitalBlue
    }

    if (amountMax is MoneyParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = "max. ${MoneyFormatter.format(amountMax.amount)}",
            color = SoyuzGrey
        )
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.displaySmall,
        maxLines = 1,
        text = amountString,
        color = amountColor,
    )
    if (amountMin is MoneyParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = "min. ${MoneyFormatter.format(amountMin.amount)}",
            color = SoyuzGrey
        )
    }
}
