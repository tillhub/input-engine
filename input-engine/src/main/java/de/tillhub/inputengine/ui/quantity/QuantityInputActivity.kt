package de.tillhub.inputengine.ui.quantity

import AppTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.Quantity
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue

@ExperimentalMaterial3Api
class QuantityInputActivity : ComponentActivity() {

    private val viewModel by viewModels<QuantityInputViewModel>()

    private val request: QuantityInputRequest by lazy {
        intent.extras?.parcelable<QuantityInputRequest>(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("$TAG: Argument QuantityInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setInitialValue(
            initialValue = Quantity.of(request.quantity),
            currentValue = Quantity.of(request.quantity),
            minValue = request.minQuantity,
            maxValue = request.maxQuantity
        )

        val title = when (val stringParam = request.toolbarTitle) {
            is StringParam.String -> stringParam.value
            is StringParam.StringResource -> getString(stringParam.resIdRes)
        }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val displayData by viewModel.displayDataFlow.collectAsStateWithLifecycle()
            QuantityScreen(title, snackbarHostState, displayData)
        }
    }

    @Composable
    fun QuantityScreen(
        title: String,
        snackbarHostState: SnackbarHostState,
        displayData: QuantityInputData
    ) {
        AppTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { Snackbar(it) }
                    )
                },
                topBar = {
                    Toolbar(title) {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    QuantityPreview(
                        quantity = displayData.qty,
                        quantityHint = request.quantityHint,
                        minQuantity = request.minQuantity,
                        maxQuantity = request.maxQuantity,
                        decrease = { viewModel.decrease() },
                        increase = { viewModel.increase() }
                    )
                    Numpad(
                        onClick = viewModel::processKey,
                        showDecimalSeparator = true
                    )
                    SubmitButton(displayData.isValid) {
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(
                                ExtraKeys.EXTRAS_RESULT,
                                QuantityInputResult.Success(
                                    displayData.qty.decimal,
                                )
                            )
                        })
                        finish()
                    }
                }
            }
        }
    }

    @Suppress("LongParameterList", "LongMethod")
    @ExperimentalMaterial3Api
    @Composable
    fun QuantityPreview(
        quantity: Quantity,
        quantityHint: QuantityParam,
        decrease: () -> Unit,
        increase: () -> Unit,
        maxQuantity: QuantityParam,
        minQuantity: QuantityParam
    ) {
        val (quantityString, quantityColor) = if (quantityHint is QuantityParam.Enable && quantity.isZero()) {
            QuantityFormatter.format(Quantity.of(quantityHint.value)) to MagneticGrey
        } else {
            QuantityFormatter.formatPlain(quantity) to OrbitalBlue
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier.wrapContentWidth(Alignment.Start),
                    onClick = decrease
                ) {
                    Icon(
                        modifier = Modifier
                            .width(42.dp)
                            .height(42.dp)
                            .padding(3.dp),
                        painter = painterResource(id = R.drawable.ic_minus),
                        tint = OrbitalBlue,
                        contentDescription = "contentDescription"
                    )
                }
                if (minQuantity is QuantityParam.Enable) {
                    Text(
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        text = stringResource(
                            id = R.string.min_value,
                            QuantityFormatter.format(
                                Quantity.of(minQuantity.value)
                            )
                        ),
                        color = MagneticGrey,
                    )
                }

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 2,
                    text = quantityString,
                    color = quantityColor,
                )
                if (maxQuantity is QuantityParam.Enable) {
                    Text(
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        text = stringResource(
                            id = R.string.max_value,
                            QuantityFormatter.format(
                                Quantity.of(maxQuantity.value)
                            )
                        ),
                        color = MagneticGrey,
                    )
                }
                IconButton(
                    modifier = Modifier.wrapContentWidth(Alignment.End),
                    onClick = increase
                ) {
                    Icon(
                        modifier = Modifier
                            .width(42.dp)
                            .height(42.dp)
                            .padding(3.dp),
                        painter = painterResource(id = R.drawable.ic_plus),
                        tint = OrbitalBlue,
                        contentDescription = "contentDescription"
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "QuantityInputActivity"
    }
}
