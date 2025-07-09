package de.tillhub.inputengine.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.contract.rememberAmountInputLauncher
import de.tillhub.inputengine.contract.rememberPercentageInputLauncher
import de.tillhub.inputengine.contract.rememberPinInputLauncher
import de.tillhub.inputengine.contract.rememberQuantityInputLauncher
import de.tillhub.inputengine.data.CurrencyIO
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.sample.theme.InputEngineTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<InputScreen?>(null) }

    val eur = CurrencyIO.forCode("EUR")
    val result = remember { mutableStateOf("No result") }
    val amountInputLauncher = rememberAmountInputLauncher(
        onResult = {
            when (it) {
                is AmountInputResult.Success -> {
                    result.value = it.amount.toString() + "\n" + it.extras.toString()
                }

                is AmountInputResult.Canceled -> Unit
            }
        },
    )

    val percentageInputLauncher = rememberPercentageInputLauncher(
        onResult = {
            result.value = when (it) {
                PercentageInputResult.Canceled -> "Percent action canceled"
                is PercentageInputResult.Success -> it.percent.value.toString()
            }
        },
    )

    val pinInputLauncher = rememberPinInputLauncher(
        onResult = {
            result.value = when (it) {
                PinInputResult.Canceled -> "Pin action canceled"
                is PinInputResult.Success -> "PIN entry successful"
            }
        },
    )
    val quantityInputLauncher = rememberQuantityInputLauncher(
        onResult = {
            result.value = when (it) {
                QuantityInputResult.Canceled -> "Quantity action canceled"
                is QuantityInputResult.Success -> it.quantity.toString()
            }
        },
    )
    InputEngineTheme {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp),
        ) {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                            .wrapContentWidth(),
                        text = "Results : ${result.value}",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                InputSection("Money Input") {
                    currentScreen = InputScreen.Money
                }
                Spacer(modifier = Modifier.height(16.dp))

                InputSection("Pin Input") { currentScreen = InputScreen.Pin }
                Spacer(modifier = Modifier.height(16.dp))

                InputSection("Quantity Input") { currentScreen = InputScreen.Quantity }
                Spacer(modifier = Modifier.height(16.dp))

                InputSection("Percentage Input") { currentScreen = InputScreen.Percentage }
            }

            when (currentScreen) {
                InputScreen.Money -> LaunchedEffect(Unit) {
                    amountInputLauncher.launchAmountInput(
                        request = AmountInputRequest(
                            amount = MoneyIO.of(100, eur),
                            amountMin = MoneyParam.Enable(MoneyIO.of(-30_00, eur)),
                            amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)),
                            extras = mapOf("extraArg" to "argument value"),
                        ),
                    )
                    currentScreen = null
                }

                InputScreen.Percentage -> LaunchedEffect(Unit) {
                    percentageInputLauncher.launchPercentageInput(
                        request = PercentageInputRequest(
                            percent = PercentIO.ZERO,
                            percentageMin = PercentageParam.Enable(PercentIO.ZERO),
                            percentageMax = PercentageParam.Enable(PercentIO.WHOLE),
                            allowsZero = false,
                        ),
                    )
                    currentScreen = null
                }

                InputScreen.Pin -> LaunchedEffect(Unit) {
                    pinInputLauncher.launchPinInput(
                        request = PinInputRequest(
                            pin = "9876",
                            extras = mapOf("argPin" to "hint for pin"),
                        ),
                    )
                    currentScreen = null
                }

                InputScreen.Quantity -> LaunchedEffect(Unit) {
                    quantityInputLauncher.launchQuantityInput(
                        QuantityInputRequest(
                            quantity = QuantityIO.ZERO,
                            minQuantity = QuantityParam.Enable(-QuantityIO.of(50)),
                            maxQuantity = QuantityParam.Enable(QuantityIO.of(50)),
                            hintQuantity = QuantityParam.Enable(QuantityIO.of(BigDecimal.TEN)),
                        ),
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun InputSection(
    label: String,
    onClick: () -> Unit,
) {
    Column {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
        ) {
            Text(text = label)
        }
    }
}

enum class InputScreen {
    Money, Pin, Quantity, Percentage
}
