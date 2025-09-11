package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import de.tillhub.inputengine.resources.ic_minus
import de.tillhub.inputengine.resources.ic_plus
import de.tillhub.inputengine.resources.max_value
import de.tillhub.inputengine.resources.min_value
import de.tillhub.inputengine.theme.hintColor
import de.tillhub.inputengine.ui.QuantityInputData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuantityInputPreview(
    modifier: Modifier = Modifier,
    quantity: QuantityInputData,
    minQuantity: StringParam,
    maxQuantity: StringParam,
    decrease: () -> Unit,
    increase: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FilledIconButton(
                modifier = Modifier.size(48.dp).semantics { contentDescription = "Button decrease" },
                shape = MaterialTheme.shapes.medium,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = decrease,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_minus),
                    contentDescription = "Decrease",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Text(
                text = quantity.text,
                modifier = Modifier.padding(16.dp).semantics { contentDescription = "Current quantity" },
                style = MaterialTheme.typography.displayLarge,
                color =
                if (quantity.isHint) {
                    MaterialTheme.colorScheme.hintColor
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
            )
            FilledIconButton(
                modifier = Modifier.size(48.dp).semantics { contentDescription = "Button increase" },
                shape = MaterialTheme.shapes.medium,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = increase,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_plus),
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }

        if (minQuantity is StringParam.Enable) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp).semantics { contentDescription = "Min allowed quantity" },
                text = stringResource(Res.string.min_value, minQuantity.value),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }

        if (maxQuantity is StringParam.Enable) {
            Text(
                modifier = Modifier.semantics { contentDescription = "Max allowed quantity" },
                style = MaterialTheme.typography.labelSmall,
                text = stringResource(Res.string.max_value, maxQuantity.value),
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}
