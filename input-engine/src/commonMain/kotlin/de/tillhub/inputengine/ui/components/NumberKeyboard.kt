package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.formatting.DecimalFormatter
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_button_clear
import de.tillhub.inputengine.resources.numpad_button_delete
import de.tillhub.inputengine.resources.numpad_button_negative
import de.tillhub.inputengine.theme.GalacticBlue
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NumberKeyboard(
    modifier: Modifier = Modifier,
    showDecimalSeparator: Boolean = false,
    showNegative: Boolean = false,
    decimalSeparator: String = DecimalFormatter.decimalSeparator.toString(),
    onClick: (NumpadKey) -> Unit = {},
) {
    val (leftActionContentDescription, leftActionText) = getLeftActionLabel(
        showDecimalSeparator,
        showNegative,
        decimalSeparator,
        stringResource(Res.string.numpad_button_negative),
        stringResource(Res.string.numpad_button_clear),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
        ) {
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 7"
                },
                number = Digit.SEVEN,
                onClick = onClick
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 8"
                },
                number = Digit.EIGHT,
                onClick = onClick,
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 9"
                },
                number = Digit.NINE,
                onClick = onClick,
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
        ) {
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 4"
                },
                number = Digit.FOUR,
                onClick = onClick,
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 5"
                },
                number = Digit.FIVE,
                onClick = onClick,
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 6"
                },
                number = Digit.SIX,
                onClick = onClick,
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
        ) {
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 1"
                },
                number = Digit.ONE,
                onClick = onClick,
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 2"
                },
                number = Digit.TWO,
                onClick = onClick,
            )
            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 3"
                },
                number = Digit.THREE,
                onClick = onClick,
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
        ) {
            DoubleActionButton(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(BUTTON_ASPECT_RATIO)
                    .padding(6.dp),
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
            ) {
                Text(
                    modifier = Modifier.semantics {
                        contentDescription = leftActionContentDescription
                    },
                    text = leftActionText,
                    fontSize = 14.sp,
                    color = GalacticBlue,
                )
            }

            NumberButton(
                modifier = Modifier.weight(1f).semantics {
                    contentDescription = "Number button 0"
                },
                number = Digit.ZERO,
                onClick = onClick,
            )

            DoubleActionButton(
                onClick = { onClick(NumpadKey.Delete) },
                onLongClick = { onClick(NumpadKey.Clear) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(BUTTON_ASPECT_RATIO)
                    .padding(6.dp),
            ) {
                Text(
                    modifier = Modifier.semantics {
                        contentDescription = "Delete"
                    },
                    text = stringResource(resource = Res.string.numpad_button_delete),
                    fontSize = 24.sp,
                    color = GalacticBlue,
                    fontWeight = FontWeight.Bold,
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
    clearLabel: String,
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
