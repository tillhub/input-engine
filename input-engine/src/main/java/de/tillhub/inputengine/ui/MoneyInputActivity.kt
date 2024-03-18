package de.tillhub.inputengine.ui

import AppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.tillhub.inputengine.R
import de.tillhub.inputengine.contract.ExtraKeys
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.helper.parcelable
import de.tillhub.inputengine.ui.theme.ExtraButtonTint
import de.tillhub.inputengine.ui.theme.GalacticBlue
import de.tillhub.inputengine.ui.theme.LunarGray
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import de.tillhub.inputengine.ui.theme.SoyuzGrey
import de.tillhub.inputengine.ui.theme.Tint
import java.math.BigDecimal

@ExperimentalMaterial3Api
class MoneyInputActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MoneyInputActivity"
    }

    private val viewModel by viewModels<MoneyInputViewModel>()

    private val request: MoneyInputRequest by lazy {
        intent.extras?.parcelable<MoneyInputRequest>(ExtraKeys.EXTRA_REQUEST)
            ?: throw IllegalArgumentException("$TAG: Argument MoneyInputRequest is missing")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(request)
        setContent {
            val money by viewModel.moneyInput.collectAsStateWithLifecycle()
            val color = if (money.isValid) OrbitalBlue else MagneticGrey
            AppTheme {
                Scaffold(
                    topBar = {
                        Column {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors()
                                    .copy(containerColor = Color.White),
                                title = {
                                    Text(
                                        request.title,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "close"
                                        )
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    },
                    bottomBar = {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RectangleShape,
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color
                            )
                        ) {
                            Text(
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = "Next"
                            )
                        }
                    }
                ) {
                    CustomNumberKeyboard(
                        padding = it,
                        money = money,
                        amountMin = request.amountMin,
                        amountMax = request.amountMax,
                        previous = request.amountPrevious,
                        onClick = viewModel::input
                    )
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    fun CustomNumberKeyboard(
        padding: PaddingValues,
        money: MoneyInputData,
        amountMin: BigDecimal?,
        amountMax: BigDecimal?,
        previous: BigDecimal?,
        onClick: (NumpadKey) -> Unit
    ) {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    amountMin?.let {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            text = "min.\n$it",
                            color = SoyuzGrey
                        )
                    }
                    Text(
                        style = MaterialTheme.typography.displaySmall,
                        maxLines = 1,
                        text = money.text,
                        color = OrbitalBlue,
                    )
                    amountMax?.let {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            text = "max.\n$it",
                            color = SoyuzGrey
                        )
                    }
                }
                previous?.let {
                    Text(
                        text = "Previous: $it",
                        fontSize = 14.sp,
                        color = OrbitalBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberButton(number = 7, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 8, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 9, onClick = onClick, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberButton(number = 4, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 5, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 6, onClick = onClick, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberButton(number = 1, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 2, onClick = onClick, modifier = Modifier.weight(1f))
                    NumberButton(number = 3, onClick = onClick, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExtraButton(
                        onClick = { onClick(NumpadKey.Clear) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.numpad_button_clear),
                            fontSize = 14.sp,
                            color = GalacticBlue,
                        )
                    }
                    NumberButton(number = 0, onClick = onClick, modifier = Modifier.weight(1f))
                    ExtraButton(
                        onClick = { onClick(NumpadKey.Delete) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.numpad_button_delete),
                            fontSize = 24.sp,
                            color = GalacticBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NumberButton(
        modifier: Modifier = Modifier,
        onClick: (NumpadKey) -> Unit,
        number: Int
    ) {
        OutlinedButton(
            onClick = {
                onClick(NumpadKey.SingleDigit(Digit.from(number)))
            },
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(
                width = 1.0.dp,
                color = LunarGray,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 1.dp,
                disabledElevation = 1.dp,
                hoveredElevation = 1.dp,
                focusedElevation = 1.dp
            ),
            modifier = modifier
                .aspectRatio(1.25f)
                .padding(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Tint),
        ) {
            Text(
                text = number.toString(),
                fontSize = 14.sp,
                color = GalacticBlue
            )
        }
    }

    @Composable
    private fun ExtraButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        buttonText: @Composable () -> Unit
    ) {
        OutlinedButton(
            onClick = {
                onClick()
            },
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(
                width = 1.0.dp,
                color = LunarGray,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 1.dp,
                disabledElevation = 1.dp,
                hoveredElevation = 1.dp,
                focusedElevation = 1.dp
            ),
            modifier = modifier
                .aspectRatio(1.25f)
                .padding(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ExtraButtonTint),
        ) {
            buttonText()
        }
    }
}
