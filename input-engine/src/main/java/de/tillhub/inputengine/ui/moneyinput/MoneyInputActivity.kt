package de.tillhub.inputengine.ui.moneyinput

import AppTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.BundleCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.ui.components.SubmitButton
import de.tillhub.inputengine.ui.components.Numpad
import de.tillhub.inputengine.ui.components.Toolbar
import de.tillhub.inputengine.ui.components.getModifierBasedOnDeviceType
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.EMPTY
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import de.tillhub.inputengine.ui.theme.TabletScaffoldModifier

@ExperimentalMaterial3Api
class MoneyInputActivity : ComponentActivity() {

    private val viewModel by viewModels<MoneyInputViewModel>()

    private val request: AmountInputRequest by lazy {
        intent.extras?.let {
            BundleCompat.getParcelable(it, ExtraKeys.EXTRA_REQUEST, AmountInputRequest::class.java)
        } ?: throw IllegalArgumentException("Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(request)
        setContent {
            val title = when (val stringParam = request.toolbarTitle) {
                is StringParam.String -> stringParam.value
                is StringParam.StringResource -> stringResource(id = stringParam.resIdRes)
            }

            val amountMin by viewModel.uiMinValue.collectAsStateWithLifecycle()
            val amountMax by viewModel.uiMaxValue.collectAsStateWithLifecycle()
            val amount by viewModel.moneyInput.collectAsStateWithLifecycle()
            AppTheme {
                Scaffold(
                    modifier = getModifierBasedOnDeviceType(
                        isTablet = TabletScaffoldModifier,
                        isMobile = Modifier
                    ),
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
                            amountMin = amountMin,
                            amountMax = amountMax,
                            amountHint = request.hintAmount
                        )
                        Numpad(
                            onClick = viewModel::input,
                            showNegative = true
                        )
                        SubmitButton(amount.isValid) {
                            setResult(RESULT_OK, Intent().apply {
                                putExtra(
                                    ExtraKeys.EXTRAS_RESULT,
                                    AmountInputResult.Success(amount.money, request.extras)
                                )
                            })
                            finish()
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun InputPreview(
        amount: MoneyInputData = EMPTY,
        amountMin: MoneyParam = MoneyParam.Disable,
        amountMax: MoneyParam = MoneyParam.Disable,
        amountHint: MoneyParam = MoneyParam.Disable
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
                text = "max. ${MoneyFormatter.format(amountMax.amount)}",
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
                text = "min. ${MoneyFormatter.format(amountMin.amount)}",
                color = SoyuzGrey
            )
        }
    }
}