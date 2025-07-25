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
import de.tillhub.inputengine.theme.hintColor
import de.tillhub.inputengine.ui.MoneyInputData
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AmountInputPreview(
    modifier: Modifier = Modifier,
    amount: MoneyInputData,
    amountMin: StringParam = StringParam.Disable,
    amountMax: StringParam = StringParam.Disable,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(16.dp).semantics { contentDescription = "Current amount" },
            style = MaterialTheme.typography.displayLarge,
            maxLines = 1,
            text = amount.text,
            color =
            if (amount.isHint) {
                MaterialTheme.colorScheme.hintColor
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        )

        if (amountMin is StringParam.Enable) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp).semantics { contentDescription = "Min allowed amount" },
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                text = stringResource(Res.string.min_value, amountMin.value),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }

        if (amountMax is StringParam.Enable) {
            Text(
                modifier = Modifier.semantics { contentDescription = "Max allowed amount" },
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                text = stringResource(Res.string.max_value, amountMax.value),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}
