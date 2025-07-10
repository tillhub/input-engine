package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import de.tillhub.inputengine.theme.MagneticGrey
import de.tillhub.inputengine.theme.OrbitalBlue
import org.jetbrains.compose.resources.stringResource

@Composable
fun PercentageInputPreview(
    percentText: String,
    percentageMin: StringParam = StringParam.Disable,
    percentageMax: StringParam = StringParam.Disable,
) {
    if (percentageMax is StringParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .semantics { contentDescription = "Max allowed percentage" },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(Res.string.max_value, percentageMax.value),
            color = MagneticGrey,
        )
    }

    Text(
        modifier = Modifier.fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .semantics { contentDescription = "Current percentage" },
        style = MaterialTheme.typography.displaySmall,
        maxLines = 1,
        text = percentText,
        color = OrbitalBlue,
    )

    if (percentageMin is StringParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .semantics { contentDescription = "Min allowed percentage" },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(Res.string.min_value, percentageMin.value),
            color = MagneticGrey,
        )
    }
}
