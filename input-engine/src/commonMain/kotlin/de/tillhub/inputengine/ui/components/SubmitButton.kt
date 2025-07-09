package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_button_submit
import de.tillhub.inputengine.theme.MagneticGrey
import de.tillhub.inputengine.theme.OrbitalBlue
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SubmitButton(
    isEnable: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        enabled = isEnable,
        modifier = modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnable) OrbitalBlue else MagneticGrey,
        ),
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .semantics { contentDescription = "Submit button label" },
            style = MaterialTheme.typography.headlineSmall,
            text = stringResource(resource = Res.string.numpad_button_submit),
        )
    }
}
