package de.tillhub.inputengine.ui.screens

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
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_quantity
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.TabletScaffoldModifier
import de.tillhub.inputengine.ui.QuantityInputViewModel
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.QuantityInputPreview
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuantityInputScreen(
    onResult: (QuantityInputResult) -> Unit,
    viewModel: QuantityInputViewModel,
) {
    val title = when (val title = viewModel.toolbarTitle) {
        StringParam.Disable -> stringResource(Res.string.numpad_title_quantity)
        is StringParam.Enable -> title.value
    }
    val displayData by viewModel.quantityInputFlow.collectAsState()

    AppTheme {
        Scaffold(
            modifier =
            getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(
                    title = title,
                    onBackClick = { onResult(QuantityInputResult.Canceled) },
                )
            },
        ) { innerPadding ->
            Column(
                modifier =
                Modifier
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
