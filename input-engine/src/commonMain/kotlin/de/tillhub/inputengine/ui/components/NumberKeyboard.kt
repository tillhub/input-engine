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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.formatter.DecimalFormatter
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_button_clear
import de.tillhub.inputengine.resources.numpad_button_delete
import de.tillhub.inputengine.resources.numpad_button_negative
import de.tillhub.inputengine.ui.theme.GalacticBlue
import de.tillhub.inputengine.ui.theme.LunarGray
import de.tillhub.inputengine.ui.theme.Tint
import de.tillhub.inputengine.ui.theme.buttonElevation
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Suppress("LongMethod")
@Preview
@Composable
internal fun Numpad(
    onClick: (NumpadKey) -> Unit = {},
    showDecimalSeparator: Boolean = false,
    showNegative: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
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
                            "${DecimalFormatter.decimalSeparator}\n${
                                stringResource(
                                    Res.string.numpad_button_negative
                                )
                            }"
                        }
                        showDecimalSeparator -> {
                            DecimalFormatter.decimalSeparator.toString()
                        }
                        showNegative -> stringResource(Res.string.numpad_button_negative)
                        else -> stringResource(Res.string.numpad_button_clear)
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
                    text = stringResource(resource = Res.string.numpad_button_delete),
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