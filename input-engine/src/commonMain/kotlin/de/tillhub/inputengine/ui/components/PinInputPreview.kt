package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.pin_enter
import de.tillhub.inputengine.theme.HintGray
import de.tillhub.inputengine.theme.OrbitalBlue
import de.tillhub.inputengine.theme.textFieldTransparentColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun PinInputPreview(
    pinText: String,
    hintText: String,
    overridePinInput: Boolean,
    onOverride: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            readOnly = true,
            modifier =
            Modifier
                .align(Alignment.Center)
                .testTag("Pin placeholder"),
            value = pinText,
            onValueChange = { },
            textStyle =
            TextStyle.Default.copy(
                color = OrbitalBlue,
                fontSize = 64.sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style =
                    TextStyle.Default.copy(
                        color = HintGray,
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center,
                    ),
                    text = hintText,
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldTransparentColors(),
        )
        if (overridePinInput) {
            Text(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .clickable {
                        onOverride()
                    }.semantics { contentDescription = "Override pin" },
                textAlign = TextAlign.End,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(Res.string.pin_enter),
                color = OrbitalBlue,
            )
        }
    }
}
