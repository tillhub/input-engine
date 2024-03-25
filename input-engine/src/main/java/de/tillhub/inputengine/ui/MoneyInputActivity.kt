package de.tillhub.inputengine.ui

import AppTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.components.NumberKeyboard
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue

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
            val title = (request as? MoneyInputRequest.WithTitleStringRes)?.let {
                stringResource(id = it.toolbarTitle)
            } ?: (request as MoneyInputRequest.WithTitleString).toolbarTitle

            val moneyInput by viewModel.moneyInput.collectAsStateWithLifecycle()
            val color = if (moneyInput.isValid) OrbitalBlue else MagneticGrey
            AppTheme {
                Scaffold(
                    topBar = {
                        Toolbar(title)
                    },
                    bottomBar = {
                        SubmitButton(moneyInput, color)
                    }
                ) {
                    NumberKeyboard(
                        padding = it,
                        money = moneyInput,
                        amountMin = request.amountMin,
                        amountMax = request.amountMax,
                        hint = request.hintAmount,
                        onClick = viewModel::input
                    )
                }
            }
        }
    }

    @Composable
    private fun SubmitButton(
        moneyInput: MoneyInputData,
        color: Color
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            onClick = {
                val resultIntent = Intent()
                resultIntent.putExtra("money", moneyInput.price)
                setResult(MONEY_INPUT_RESULT, resultIntent)
                finish()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = color
            )
        ) {
            Text(
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = R.string.numpad_button_submit)
            )
        }
    }

    @Composable
    private fun Toolbar(title: String) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.White),
                title = {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close"
                        )
                    }
                }
            )
            HorizontalDivider()
        }
    }

    companion object {
        private const val TAG = "MoneyInputActivity"
        const val MONEY_INPUT_RESULT = 1000
    }
}