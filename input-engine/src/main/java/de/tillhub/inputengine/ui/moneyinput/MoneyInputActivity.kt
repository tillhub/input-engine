package de.tillhub.inputengine.ui.moneyinput

import AppTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import java.util.Currency

@ExperimentalMaterial3Api
class MoneyInputActivity : ComponentActivity() {

    private val viewModel by viewModels<MoneyInputViewModel>()

    private val request: AmountInputRequest by lazy {
        intent.extras?.parcelable<AmountInputRequest>(ExtraKeys.EXTRA_REQUEST)
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
            AppTheme {
                Scaffold(
                    topBar = {
                        Toolbar(title) {
                            finish()
                        }
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
                            currency = request.currency,
                            amountMin = request.amountMin,
                            amountMax = request.amountMax,
                            amountHint = request.hintAmount
                        )
                        Numpad(viewModel::input)
                        SubmitButton(amount.isValid) {
                            setResult(Activity.RESULT_OK, Intent().apply {
                                putExtra(
                                    ExtraKeys.EXTRAS_RESULT,
                                    AmountInputResult.Success(amount.money.value, request.extra)
                                )
                            })
                            finish()
                        }
                    }
                }
            }
        }
    }

    @Suppress("LongParameterList")
    @ExperimentalMaterial3Api
    @Composable
    fun InputPreview(
        amount: MoneyInputData,
        currency: Currency,
        amountMin: MoneyParam,
        amountMax: MoneyParam,
        amountHint: MoneyParam
    ) {
        val (amountString, amountColor) = if (amountHint is MoneyParam.Enable && amount.money.isZero()) {
            MoneyFormatter.format(amountHint.amount, currency) to MagneticGrey
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
                text = "max. ${MoneyFormatter.format(amountMax.amount, currency)}",
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
                text = "min. ${MoneyFormatter.format(amountMin.amount, currency)}",
                color = SoyuzGrey
            )
        }
    }

    companion object {
        private const val TAG = "MoneyInputActivity"
    }
}