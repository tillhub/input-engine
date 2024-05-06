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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.BundleCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier

@ExperimentalMaterial3Api
class QuantityInputActivity : ComponentActivity() {

    private val viewModel by viewModels<QuantityInputViewModel>()

    private val request: QuantityInputRequest by lazy {
        intent.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRA_REQUEST, QuantityInputRequest::class.java)
        } ?: throw IllegalArgumentException("Argument QuantityInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setInitialValue(request)

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
                modifier = getModifierBasedOnDeviceType(
                    isTablet = TabletScaffoldModifier,
                    isMobile = Modifier
                ),
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
                        quantityText = displayData.text,
                        quantityColor = displayData.color,
                        minQuantity = request.minQuantity,
                        maxQuantity = request.maxQuantity,
                        decrease = { viewModel.decrease() },
                        increase = { viewModel.increase() }
                    )
                    Numpad(
                        onClick = viewModel::processKey,
                        showDecimalSeparator = true,
                        showNegative = true
                    )
                    SubmitButton(displayData.isValid) {
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(
                                ExtraKeys.EXTRAS_RESULT,
                                QuantityInputResult.Success(
                                    displayData.qty,
                                    request.extras
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
    @Composable
    fun QuantityPreview(
        quantityText: String,
        quantityColor: Color,
        decrease: () -> Unit,
        increase: () -> Unit,
        maxQuantity: QuantityParam,
        minQuantity: QuantityParam
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
                            .height(42.dp),
                        painter = painterResource(id = R.drawable.ic_minus),
                        tint = OrbitalBlue,
                        contentDescription = "contentDescription"
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (maxQuantity is QuantityParam.Enable) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            text = stringResource(
                                id = R.string.max_value,
                                QuantityFormatter.format(maxQuantity.value)
                            ),
                            color = MagneticGrey,
                        )
                    }
                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        maxLines = 1,
                        text = quantityText,
                        color = quantityColor,
                    )
                    if (minQuantity is QuantityParam.Enable) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            text = stringResource(
                                id = R.string.min_value,
                                QuantityFormatter.format(minQuantity.value)
                            ),
                            color = MagneticGrey,
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.wrapContentWidth(Alignment.End),
                    onClick = increase
                ) {
                    Icon(
                        modifier = Modifier
                            .width(42.dp)
                            .height(42.dp),
                        painter = painterResource(id = R.drawable.ic_plus),
                        tint = OrbitalBlue,
                        contentDescription = "contentDescription"
                    )
                }
            }
        }
    }
}
