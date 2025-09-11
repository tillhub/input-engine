package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_button_submit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SubmitButton(
    isEnable: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        enabled = isEnable,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Submit button" },
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Text(
            modifier =
            Modifier
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .semantics { contentDescription = "Submit button label" },
            style = MaterialTheme.typography.labelMedium,
            text = stringResource(resource = Res.string.numpad_button_submit),
        )
    }
}

@Preview
@Composable
private fun SubmitButtonPreview() {
    SubmitButtonPreview()
}
