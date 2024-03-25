package de.tillhub.inputengine.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.tillhub.inputengine.contract.MoneyInputContract
import de.tillhub.inputengine.contract.MoneyInputRequest

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
                            MoneyInputRequest.WithTitleStringRes(
                                toolbarTitle = R.string.numpad_keyboard_title,
                                amountMin = 11.toBigDecimal(),
                                amount = 10.toBigDecimal(),
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