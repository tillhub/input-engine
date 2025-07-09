package de.tillhub.inputengine.ui.quantity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.allStringResources
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.QuantityInputPreview
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuantityInputScreen(
    onResult: (QuantityInputResult) -> Unit,
    viewModel: QuantityInputViewModel,
) {
    val displayData by viewModel.displayDataFlow.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = stringResource(Res.allStringResources.getValue(viewModel.toolbarTitle)),
                    onBackClick = { onResult(QuantityInputResult.Canceled) },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                QuantityInputPreview(
                    quantity = displayData,
                    minQuantity = viewModel.minStringParam,
                    maxQuantity = viewModel.maxStringParam,
                    increase = viewModel::increase,
                    decrease = viewModel::decrease,
                )

                NumberKeyboard(
                    showDecimalSeparator = viewModel.allowDecimal,
                    showNegative = viewModel.allowNegative,
                    onClick = viewModel::processKey,
                )

                SubmitButton(displayData.isValid) {
                    onResult(
                        QuantityInputResult.Success(
                            quantity = displayData.qty,
                            extras = viewModel.responseExtras,
                        ),
                    )
                }
            }
        }
    }
}