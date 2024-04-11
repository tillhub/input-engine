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
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.PinInputContract
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.contract.QuantityInputContract
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.sample.ui.theme.InputEngineTheme
import java.math.BigDecimal
import java.util.Currency

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var moneyInputLauncher: ActivityResultLauncher<AmountInputRequest>
    private lateinit var pinInputLauncher: ActivityResultLauncher<PinInputRequest>
    private lateinit var quantityInputLauncher: ActivityResultLauncher<QuantityInputRequest>
    private var scanCode = mutableStateOf("")
    private var pinResult = mutableStateOf("")
    private var quantityResult = mutableStateOf("")

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moneyInputLauncher = registerForActivityResult(
            AmountInputContract(),
            activityResultRegistry
        ) {
            if (it is AmountInputResult.Success) scanCode.value = it.amount.toPlainString()
        }

        pinInputLauncher = registerForActivityResult(
            PinInputContract(),
            activityResultRegistry
        ) {
            pinResult.value = when (it) {
                PinInputResult.Canceled -> getString(de.tillhub.inputengine.R.string.pin_wrong)
                PinInputResult.Success -> getString(de.tillhub.inputengine.R.string.pin_correct)
            }
        }
        quantityInputLauncher = registerForActivityResult(
            QuantityInputContract(),
            activityResultRegistry
        ) {
            quantityResult.value = when (it) {
                QuantityInputResult.Canceled -> getString(de.tillhub.inputengine.R.string.incorrect_quantity)
                is QuantityInputResult.Success -> it.quantity.toString()
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
                                        AmountInputRequest(
                                            amountMin = MoneyParam.Enable(100.toBigInteger()),
                                            amountMax = MoneyParam.Enable(200000000.toBigInteger()),
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
                                        PinInputRequest(pin = "12345", overridePinInput = true)
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
                        Spacer(modifier = Modifier.padding(16.dp))
                        Column(verticalArrangement = Arrangement.SpaceBetween) {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    quantityInputLauncher.launch(
                                        QuantityInputRequest(
                                            quantity = BigDecimal.ZERO,
                                            minQuantity = QuantityParam.Enable(BigDecimal.ZERO),
                                            maxQuantity = QuantityParam.Enable(10000.0.toBigDecimal()),
                                            quantityHint = QuantityParam.Enable(BigDecimal.TEN)
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Quantity Input",
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = quantityResult.value)
                        }
                    }
                }
            }
        }
    }
}