package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.R
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.ui.theme.buttonElevation
import de.tillhub.inputengine.ui.theme.GalacticBlue
import de.tillhub.inputengine.ui.theme.LunarGray
import de.tillhub.inputengine.ui.theme.Tint
import java.text.DecimalFormatSymbols

@Suppress("LongMethod")
@Preview
@Composable
internal fun Numpad(
    onClick: (NumpadKey) -> Unit = {},
    showDecimalSeparator: Boolean = false,
    showNegative: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            NumberButton(number = 7, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 8, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 9, onClick = onClick, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            NumberButton(number = 4, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 5, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 6, onClick = onClick, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            NumberButton(number = 1, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 2, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = 3, onClick = onClick, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            DoubleActionButton(
                onClick = {
                    when {
                        showDecimalSeparator -> onClick(NumpadKey.DecimalSeparator)
                        showNegative -> onClick(NumpadKey.Negate)
                        else -> onClick(NumpadKey.Clear)
                    }
                },
                onLongClick = {
                    when {
                        showNegative -> onClick(NumpadKey.Negate)
                        showDecimalSeparator -> onClick(NumpadKey.DecimalSeparator)
                        else -> onClick(NumpadKey.Clear)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(BUTTON_ASPECT_RATIO)
                    .padding(6.dp)
            ) {
                Text(
                    text = when {
                        showDecimalSeparator && showNegative -> {
                            "${DecimalFormatSymbols.getInstance().decimalSeparator}\n${
                                stringResource(
                                    R.string.numpad_button_negative
                                )
                            }"
                        }

                        showDecimalSeparator -> {
                            DecimalFormatSymbols.getInstance().decimalSeparator.toString()
                        }

                        showNegative -> stringResource(R.string.numpad_button_negative)
                        else -> stringResource(R.string.numpad_button_clear)
                    },
                    fontSize = 14.sp,
                    color = GalacticBlue,
                )
            }
            NumberButton(number = 0, onClick = onClick, modifier = Modifier.weight(1f))
            DoubleActionButton(
                onClick = { onClick(NumpadKey.Delete) },
                onLongClick = { onClick(NumpadKey.Clear) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(BUTTON_ASPECT_RATIO)
                    .padding(6.dp)
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
        modifier = modifier
            .aspectRatio(BUTTON_ASPECT_RATIO)
            .padding(6.dp),
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(width = 1.0.dp, color = LunarGray),
        elevation = buttonElevation(),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Tint),
    ) {
        Text(
            text = number.toString(),
            fontSize = 14.sp,
            color = GalacticBlue
        )
    }
}

const val BUTTON_ASPECT_RATIO = 1.25f