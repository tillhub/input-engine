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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.financial.param.MoneyParam
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.helper.rememberViewModel
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import de.tillhub.inputengine.resources.numpad_title_amount
import de.tillhub.inputengine.ui.amount.MoneyInputData.Companion.EMPTY
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun AmountInputScreen(
    request: AmountInputRequest,
    onResult: (AmountInputResult.Success) -> Unit,
    onDismiss: () -> Unit,
    viewModel: AmountInputViewModel = rememberViewModel {
        provideAmountInputViewModelFactory(request)
    },
) {
    val amountMin by viewModel.uiMinValue.collectAsState()
    val amountMax by viewModel.uiMaxValue.collectAsState()
    val amount by viewModel.moneyInput.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.numpad_title_amount),
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
                InputPreview(
                    amount = amount,
                    amountMin = amountMin,
                    amountMax = amountMax,
                    amountHint = request.hintAmount,
                )
                NumberKeyboard(
                    onClick = viewModel::input,
                    showNegative = viewModel.amountInputMode == AmountInputMode.BOTH,
                    modifier = Modifier,
                )
                SubmitButton(
                    isEnable = amount.isValid,
                ) {
                    onResult(
                        AmountInputResult.Success(
                            amount.money,
                            request.extras,
                        ),
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
    amountHint: MoneyParam = MoneyParam.Disable,
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
            text = stringResource(Res.string.max_value, MoneyFormatter.format(amountMax.amount)),
            color = SoyuzGrey,
        )
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .semantics { contentDescription = "amount_display" },
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
            text = stringResource(Res.string.min_value, MoneyFormatter.format(amountMin.amount)),
            color = SoyuzGrey,
        )
    }
}
