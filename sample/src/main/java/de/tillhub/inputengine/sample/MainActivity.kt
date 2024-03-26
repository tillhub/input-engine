package de.tillhub.inputengine.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.MoneyInputContract
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.AmountParam
import de.tillhub.inputengine.ui.moneyinput.InputResultStatus

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var moneyInputLauncher: ActivityResultLauncher<MoneyInputRequest>
    private var scanCode = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moneyInputLauncher = registerForActivityResult(
            MoneyInputContract(),
            activityResultRegistry
        ) {
            if (it is InputResultStatus.Success) scanCode.value = it.amount.toPlainString()
        }
        setContent {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column {
                    OutlinedButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            moneyInputLauncher.launch(
                                MoneyInputRequest(
                                    amountMin = AmountParam.Enable(11.toBigDecimal()),
                                    amountMax = AmountParam.Enable(20.toBigDecimal()),
                                    currency = "€",
                                    amount = 10.toBigDecimal(),
                                    hintAmount = AmountParam.Enable(2.0.toBigDecimal())
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Navigate to Money Input!",
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Text(text = scanCode.value)
                }
            }
        }
    }
}