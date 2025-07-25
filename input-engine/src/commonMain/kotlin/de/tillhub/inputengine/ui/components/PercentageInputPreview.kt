package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun PercentageInputPreview(
    modifier: Modifier = Modifier,
    percentText: String,
    percentageMin: StringParam = StringParam.Disable,
    percentageMax: StringParam = StringParam.Disable,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(16.dp).semantics { contentDescription = "Current percentage" },
            style = MaterialTheme.typography.displayLarge,
            maxLines = 1,
            text = percentText,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (percentageMin is StringParam.Enable) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp).semantics { contentDescription = "Min allowed percentage" },
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                text = stringResource(Res.string.min_value, percentageMin.value),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
        if (percentageMax is StringParam.Enable) {
            Text(
                modifier = Modifier.semantics { contentDescription = "Max allowed percentage" },
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                text = stringResource(Res.string.max_value, percentageMax.value),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}
