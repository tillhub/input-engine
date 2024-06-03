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
import androidx.core.os.bundleOf
import de.tillhub.inputengine.contract.AmountInputContract
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.PercentageInputContract
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.contract.PinInputContract
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.contract.QuantityInputContract
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.sample.ui.theme.InputEngineTheme
import java.math.BigDecimal
import java.util.Currency

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var moneyInputLauncher: ActivityResultLauncher<AmountInputRequest>
    private lateinit var pinInputLauncher: ActivityResultLauncher<PinInputRequest>
    private lateinit var quantityInputLauncher: ActivityResultLauncher<QuantityInputRequest>
    private lateinit var percentageInputLauncher: ActivityResultLauncher<PercentageInputRequest>
    private var scanCode = mutableStateOf("")
    private var pinResult = mutableStateOf("")
    private var quantityResult = mutableStateOf("")
    private var percentResult = mutableStateOf("")

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moneyInputLauncher = registerForActivityResult(
            AmountInputContract(),
            activityResultRegistry
        ) {
            if (it is AmountInputResult.Success) scanCode.value = it.amount.amount.toPlainString()
        }

        pinInputLauncher = registerForActivityResult(
            PinInputContract(),
            activityResultRegistry
        ) {
            pinResult.value = when (it) {
                PinInputResult.Canceled -> getString(de.tillhub.inputengine.R.string.pin_wrong)
                is PinInputResult.Success -> getString(de.tillhub.inputengine.R.string.pin_correct)
            }
        }
        quantityInputLauncher = registerForActivityResult(
            QuantityInputContract(),
            activityResultRegistry
        ) {
            quantityResult.value = when (it) {
                QuantityInputResult.Canceled -> getString(R.string.incorrect_quantity)
                is QuantityInputResult.Success -> it.quantity.toString()
            }
        }
        percentageInputLauncher = registerForActivityResult(
            PercentageInputContract(), activityResultRegistry
        ) {
            percentResult.value = when (it) {
                PercentageInputResult.Canceled -> "Percent action canceled"
                is PercentageInputResult.Success -> it.percent.value.toString()
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
                                            amount = MoneyIO.of(
                                                300.toBigInteger(),
                                                Currency.getInstance("EUR")
                                            ),
                                            amountMin = MoneyParam.Enable(
                                                -MoneyIO.of(
                                                    2000.toBigInteger(),
                                                    Currency.getInstance("EUR")
                                                )
                                            ),
                                            amountMax = MoneyParam.Enable(
                                                MoneyIO.of(
                                                    2000.toBigInteger(),
                                                    Currency.getInstance("EUR")
                                                )
                                            ),
                                            hintAmount = MoneyParam.Enable(
                                                MoneyIO.of(
                                                    200.toBigInteger(),
                                                    Currency.getInstance("EUR")
                                                )
                                            )
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
                                        PinInputRequest(
                                            pin = "9876",
                                            toolbarTitle = StringParam.String("PIN title"),
                                            extras = bundleOf("argPin" to "hint for pin")
                                        )
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
                                            quantity = QuantityIO.ZERO,
                                            minQuantity = QuantityParam.Enable(-QuantityIO.of(50.toBigDecimal())),
                                            maxQuantity = QuantityParam.Enable(QuantityIO.of(50.toBigDecimal())),
                                            quantityHint = QuantityParam.Enable(QuantityIO.of(BigDecimal.TEN))
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
                        Spacer(modifier = Modifier.padding(16.dp))
                        Column(verticalArrangement = Arrangement.SpaceBetween) {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    percentageInputLauncher.launch(
                                        PercentageInputRequest(
                                            percent = PercentIO.ZERO,
                                            percentageMin = PercentageParam.Enable(PercentIO.ZERO),
                                            percentageMax = PercentageParam.Enable(PercentIO.WHOLE),
                                            allowsZero = false
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Percentage Input",
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = percentResult.value)
                        }
                    }
                }
            }
        }
    }
}