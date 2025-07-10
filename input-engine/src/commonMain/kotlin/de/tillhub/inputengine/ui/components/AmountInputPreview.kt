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
import de.tillhub.inputengine.theme.SoyuzGrey
import de.tillhub.inputengine.theme.hintColor
import de.tillhub.inputengine.ui.amount.MoneyInputData
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AmountInputPreview(
    amount: MoneyInputData,
    amountMin: StringParam = StringParam.Disable,
    amountMax: StringParam = StringParam.Disable,
) {
    if (amountMax is StringParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .semantics { contentDescription = "Max allowed amount" },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(Res.string.max_value, amountMax.value),
            color = SoyuzGrey,
        )
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .semantics { contentDescription = "Current amount" },
        style = MaterialTheme.typography.displaySmall,
        maxLines = 1,
        text = amount.text,
        color = if (amount.isHint) {
            MaterialTheme.colorScheme.hintColor
        } else {
            MaterialTheme.colorScheme.primary
        },
    )

    if (amountMin is StringParam.Enable) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .semantics { contentDescription = "Min allowed amount" },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            text = stringResource(Res.string.min_value, amountMin.value),
            color = SoyuzGrey,
        )
    }
}
