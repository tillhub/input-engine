package de.tillhub.inputengine.ui.quantity

import AppTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.InputButton
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
        setContent { QuantityInputScreen() }
    }

    @Composable
    fun QuantityInputScreen() {
        val snackbarHostState = remember { SnackbarHostState() }

        val title = when (val stringParam = request.toolbarTitle) {
            is StringParam.String -> stringParam.value
            is StringParam.StringResource -> stringResource(id = stringParam.resIdRes)
        }
        val displayData by viewModel.displayDataFlow.collectAsStateWithLifecycle()
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
                },
                bottomBar = {
                    InputButton(displayData.currentValue.isValid) {
                        setResult(RESULT_OK, Intent().apply {
//                            putExtra(
//                                ExtraKeys.EXTRAS_RESULT,
//                                InputResultStatus.Success(
//                                    amount.money.value,
//                                    request.extra
//                                )
//                            )
                        })
                        finish()
                    }
                }
            ) {
                QuantityNumpad(
                    padding = it,
                    quantity = displayData.currentValue.text,
                    quantityHint = request.quantityHint,
                    decrease = { viewModel.decrease() },
                    increase = { viewModel.increase() },
                    onClick = viewModel::processKey
                )
            }
        }
    }

    @Suppress("LongParameterList", "LongMethod")
    @ExperimentalMaterial3Api
    @Composable
    fun QuantityNumpad(
        padding: PaddingValues,
        quantity: String,
        quantityHint: QuantityParam,
        decrease: () -> Unit,
        increase: () -> Unit,
        onClick: (NumpadKey) -> Unit
    ) {
        val (quantityString, quantityColor) = if (quantityHint is QuantityParam.Enable) {
            quantity to MagneticGrey
        } else {
            quantity to OrbitalBlue
        }
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
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
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.displaySmall,
                        maxLines = 1,
                        text = quantityString,
                        color = quantityColor,
                    )
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

                Spacer(modifier = Modifier.height(64.dp))
                Numpad(
                    onClick = onClick,
                    showDecimalSeparator = true
                )
            }
        }
    }

    companion object {
        private const val TAG = "QuantityInputActivity"
    }
}
