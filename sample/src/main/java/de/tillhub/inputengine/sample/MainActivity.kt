package de.tillhub.inputengine.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.AmountInputContract
import de.tillhub.inputengine.contract.AmountResultStatus
import de.tillhub.inputengine.contract.MoneyInputResultStatus
import de.tillhub.inputengine.contract.PinInputContract
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.sample.ui.theme.InputEngineTheme
import de.tillhub.inputengine.ui.moneyinput.MoneyInputResultStatus
import java.util.Currency

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var moneyInputLauncher: ActivityResultLauncher<AmountResultStatus>
    private lateinit var pinInputLauncher: ActivityResultLauncher<PinInputRequest>
    private var scanCode = mutableStateOf("")
    private var pinResult = mutableStateOf("")

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moneyInputLauncher = registerForActivityResult(
            AmountInputContract(),
            activityResultRegistry
        ) {
            if (it is MoneyInputResultStatus.Success) scanCode.value = it.amount.toPlainString()
        }

        pinInputLauncher = registerForActivityResult(
            PinInputContract(),
            activityResultRegistry
        ) {
            if (it == RESULT_OK) {
                pinResult.value = getString(de.tillhub.inputengine.R.string.pin_correct)
            } else {
                pinResult.value = getString(de.tillhub.inputengine.R.string.pin_wrong)
            }
        }
        setContent {
            InputEngineTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    moneyInputLauncher.launch(
                                        AmountResultStatus(
                                            amountMin = MoneyParam.Enable(100.toBigInteger()),
                                            amountMax = MoneyParam.Enable(2000.toBigInteger()),
                                            currency = Currency.getInstance("EUR"),
                                            amount = 200.toBigInteger(),
                                            hintAmount = MoneyParam.Enable(200.toBigInteger())
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Money Input",
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = scanCode.value)
                        }
                        Spacer(modifier = Modifier.padding(16.dp))
                        Column(verticalArrangement = Arrangement.SpaceBetween) {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    pinInputLauncher.launch(
                                        PinInputRequest("12345")
                                    )
                                }
                            ) {
                                Text(
                                    text = "Pin Input",
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = pinResult.value)
                        }
                    }
                }
            }
        }
    }
}