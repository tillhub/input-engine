package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NumberKeyboard(
    onClick: (NumpadKey) -> Unit = {},
    showDecimalSeparator: Boolean = false,
    showNegative: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (leftActionContentDescription, leftActionText) = getLeftActionLabel(
        showDecimalSeparator,
        showNegative,
        DecimalFormatter.decimalSeparator.toString(),
        stringResource(Res.string.numpad_button_negative),
        stringResource(Res.string.numpad_button_clear)
    )

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
            NumberButton(number = Digit.SEVEN, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.EIGHT, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.NINE, onClick = onClick, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            NumberButton(number = Digit.FOUR, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.FIVE, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.SIX, onClick = onClick, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
        ) {
            NumberButton(number = Digit.ONE, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.TWO, onClick = onClick, modifier = Modifier.weight(1f))
            NumberButton(number = Digit.THREE, onClick = onClick, modifier = Modifier.weight(1f))
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
                    .semantics { contentDescription = leftActionContentDescription }
                    .weight(1f)
                    .aspectRatio(BUTTON_ASPECT_RATIO)
                    .padding(6.dp)
            ) {
                Text(
                    text = leftActionText,
                    fontSize = 14.sp,
                    color = GalacticBlue,
                )
            }

            NumberButton(number = Digit.ZERO, onClick = onClick, modifier = Modifier.weight(1f)
                    .semantics { contentDescription = "Number 0" })

            DoubleActionButton(
                onClick = { onClick(NumpadKey.Delete) },
                onLongClick = { onClick(NumpadKey.Clear) },
                modifier = Modifier
                    .semantics { contentDescription = "Delete" }
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
fun getLeftActionLabel(
    showDecimalSeparator: Boolean,
    showNegative: Boolean,
    decimalSeparator: String,
    negativeLabel: String,
    clearLabel: String
): Pair<String, String> {
    return when {
        showDecimalSeparator && showNegative -> {
            "Decimal separator and Negative sign" to "$decimalSeparator\n$negativeLabel"
        }
        showDecimalSeparator -> {
            "Decimal separator" to decimalSeparator
        }
        showNegative -> {
            "Negative sign" to negativeLabel
        }
        else -> {
            "Clear" to clearLabel
        }
    }
}

