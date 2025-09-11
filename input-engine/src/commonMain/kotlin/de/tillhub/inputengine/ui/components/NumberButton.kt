package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey

@Composable
internal fun NumberButton(
    modifier: Modifier = Modifier,
    number: Digit,
    onClick: (NumpadKey) -> Unit,
) {
    Button(
        onClick = {
            onClick(NumpadKey.SingleDigit(Digit.from(number.value)))
        },
        modifier = modifier.aspectRatio(BUTTON_ASPECT_RATIO),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Text(
            text = number.value.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

const val BUTTON_ASPECT_RATIO = 1.43f
