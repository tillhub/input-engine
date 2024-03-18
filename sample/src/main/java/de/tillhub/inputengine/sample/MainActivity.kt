package de.tillhub.inputengine.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.contract.MoneyInputContract
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.sample.ui.theme.TillhubInputEngineTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var moneyInputLauncher: ActivityResultLauncher<MoneyInputRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moneyInputLauncher = registerForActivityResult(
            MoneyInputContract(),
            activityResultRegistry
        ) {
        }
        setContent {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                OutlinedButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        moneyInputLauncher.launch(
                            MoneyInputRequest(
                                title = "Price",
                                amountMin = 5.toBigDecimal(),
                                money = 10.toBigDecimal(),
                                amountMax = 20.toBigDecimal(),
                                currency = "€",
                                isZeroAllowed = false,
                            )
                        )
                    }
                ) {
                    Text(
                        text = "Navigate to Money Input!",
                    )
                }
            }
        }
    }
}