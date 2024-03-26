package de.tillhub.inputengine.ui.moneyinput

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.AmountParam
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.InputButton
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import java.util.Currency

@ExperimentalMaterial3Api
class MoneyInputActivity : ComponentActivity() {

    private val viewModel by viewModels<MoneyInputViewModel>()

    private val request: MoneyInputRequest by lazy {
        intent.extras?.parcelable<MoneyInputRequest>(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("$TAG: Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(request)
        setContent {
            val title = when (val stringParam = request.toolbarTitle) {
                is StringParam.String -> stringParam.value
                is StringParam.StringResource -> stringResource(id = stringParam.resIdRes)
            }

            val amount by viewModel.moneyInput.collectAsStateWithLifecycle()
            val color = if (amount.isValid) OrbitalBlue else MagneticGrey
            AppTheme {
                Scaffold(
                    topBar = {
                        Toolbar(title) {
                            finish()
                        }
                    },
                    bottomBar = {
                        InputButton(color) {
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(
                                    ExtraKeys.EXTRAS_RESULT,
                                    InputResultStatus.Success(amount.price.value, request.extra)
                                )
                            })
                            finish()
                        }
                    }
                ) {
                    MoneyNumpad(
                        padding = it,
                        amount = amount,
                        currency = request.currency,
                        amountMin = request.amountMin,
                        amountMax = request.amountMax,
                        amountHint = request.hintAmount,
                        onClick = viewModel::input
                    )
                }
            }
        }
    }

    @Suppress("LongParameterList")
    @ExperimentalMaterial3Api
    @Composable
    fun MoneyNumpad(
        padding: PaddingValues,
        amount: MoneyInputData,
        currency: Currency,
        amountMin: AmountParam,
        amountMax: AmountParam,
        amountHint: AmountParam,
        onClick: (NumpadKey) -> Unit
    ) {
        val (amountString, amountColor) = if (amountHint is AmountParam.Enable && amount.price.isZero()) {
            "${currency.symbol}${amountHint.amount.toPlainString()}" to MagneticGrey
        } else {
            amount.text to OrbitalBlue
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
                    if (amountMin is AmountParam.Enable) {
                        Text(
                            modifier = Modifier.wrapContentWidth(Alignment.Start),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            text = "min.\n$currency${amountMin.amount}",
                            color = SoyuzGrey
                        )
                    }
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.displaySmall,
                        maxLines = 1,
                        text = amountString,
                        color = amountColor,
                    )
                    if (amountMax is AmountParam.Enable) {
                        Text(
                            modifier = Modifier.wrapContentWidth(Alignment.End),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            text = "max.\n$currency${amountMax.amount}",
                            color = SoyuzGrey
                        )
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))
                Numpad(onClick)
            }
        }
    }

    companion object {
        private const val TAG = "MoneyInputActivity"
    }
}