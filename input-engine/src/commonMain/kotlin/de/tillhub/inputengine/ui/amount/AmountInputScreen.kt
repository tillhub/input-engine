package de.tillhub.inputengine.ui.amount

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
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.allStringResources
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
import de.tillhub.inputengine.ui.components.AmountInputPreview
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AmountInputScreen(
    onResult: (AmountInputResult.Success) -> Unit,
    onDismiss: () -> Unit,
    viewModel: AmountInputViewModel,
) {
    val amountMin by viewModel.uiMinValue.collectAsState()
    val amountMax by viewModel.uiMaxValue.collectAsState()
    val amount by viewModel.moneyInput.collectAsState()

    AppTheme {
        Scaffold(
            modifier =
            getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = stringResource(Res.allStringResources.getValue(viewModel.toolbarTitle)),
                    onBackClick = onDismiss,
                )
            },
        ) { innerPadding ->
            Column(
                modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                AmountInputPreview(
                    amount = amount,
                    amountMin = amountMin,
                    amountMax = amountMax,
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
                            amount = amount.money,
                            extras = viewModel.responseExtras,
                        ),
                    )
                }
            }
        }
    }
}
