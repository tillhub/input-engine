package de.tillhub.inputengine.ui.quantity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.financial.param.QuantityParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import de.tillhub.inputengine.helper.rememberViewModel
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.ic_minus
import de.tillhub.inputengine.resources.ic_plus
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import de.tillhub.inputengine.resources.numpad_title_quantity
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.AppTheme
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuantityInputScreen(
    request: QuantityInputRequest,
    onResult: (QuantityInputResult) -> Unit,
    viewModel: QuantityInputViewModel = rememberViewModel {
        provideQuantityInputViewModelFactory(request)
    },
) {
    val displayData by viewModel.displayDataFlow.collectAsState()

    AppTheme {
        Scaffold(
            modifier = getModifierBasedOnDeviceType(
                isTablet = TabletScaffoldModifier,
                isMobile = Modifier,
            ),
            topBar = {
                Toolbar(title = stringResource(Res.string.numpad_title_quantity)) {
                    onResult(
                        QuantityInputResult.Canceled,
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                QuantityPreview(
                    quantityText = displayData.text,
                    quantityColor = displayData.color,
                    minQuantity = request.minQuantity,
                    maxQuantity = request.maxQuantity,
                    increase = { viewModel.increase() },
                    decrease = { viewModel.decrease() },
                )

                NumberKeyboard(
                    onClick = viewModel::processKey,
                    showDecimalSeparator = request.allowDecimal,
                    showNegative = when (val minQty = request.minQuantity) {
                        QuantityParam.Disable -> true
                        is QuantityParam.Enable -> minQty.value.isNegative()
                    },
                )

                SubmitButton(displayData.isValid) {
                    onResult(
                        QuantityInputResult.Success(
                            quantity = displayData.qty,
                            extras = request.extras,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun QuantityPreview(
    quantityText: String,
    quantityColor: androidx.compose.ui.graphics.Color,
    minQuantity: QuantityParam,
    maxQuantity: QuantityParam,
    decrease: () -> Unit,
    increase: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = decrease) {
                Icon(
                    painter = painterResource(Res.drawable.ic_minus),
                    contentDescription = "Decrease",
                    tint = OrbitalBlue,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                if (maxQuantity is QuantityParam.Enable) {
                    Text(
                        text = stringResource(
                            Res.string.max_value,
                            QuantityFormatter.format(maxQuantity.value),
                        ),
                        color = MagneticGrey,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = quantityText,
                    modifier = Modifier.semantics { contentDescription = "qtyValue" },
                    color = quantityColor,
                    style = MaterialTheme.typography.displaySmall,
                )
                if (minQuantity is QuantityParam.Enable) {
                    Text(
                        text = stringResource(
                            Res.string.min_value,
                            QuantityFormatter.format(minQuantity.value),
                        ),
                        color = MagneticGrey,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            IconButton(onClick = increase) {
                Icon(
                    painter = painterResource(Res.drawable.ic_plus),
                    contentDescription = "Increase",
                    tint = OrbitalBlue,
                )
            }
        }
    }
}
