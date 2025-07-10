package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.theme.GalacticBlue
import de.tillhub.inputengine.theme.LunarGray
import de.tillhub.inputengine.theme.Tint
import de.tillhub.inputengine.theme.buttonElevation

@Composable
internal fun NumberButton(
    modifier: Modifier = Modifier,
    number: Digit,
    onClick: (NumpadKey) -> Unit,
) {
    OutlinedButton(
        onClick = {
            onClick(NumpadKey.SingleDigit(Digit.from(number.value)))
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
            text = number.value.toString(),
            fontSize = 14.sp,
            color = GalacticBlue,
        )
    }
}

const val BUTTON_ASPECT_RATIO = 1.25f
