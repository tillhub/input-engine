package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import de.tillhub.inputengine.theme.MagneticGrey
import de.tillhub.inputengine.theme.OrbitalBlue
import de.tillhub.inputengine.theme.hintColor
import de.tillhub.inputengine.ui.QuantityInputData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QuantityInputPreview(
    quantity: QuantityInputData,
    minQuantity: StringParam,
    maxQuantity: StringParam,
    decrease: () -> Unit,
    increase: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                modifier = Modifier.semantics { contentDescription = "Button decrease" },
                onClick = decrease,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_minus),
                    contentDescription = "Decrease",
                    tint = OrbitalBlue,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                if (maxQuantity is StringParam.Enable) {
                    Text(
                        modifier =
                        Modifier
                            .semantics { contentDescription = "Max allowed quantity" },
                        text =
                        stringResource(
                            Res.string.max_value,
                            maxQuantity.value,
                        ),
                        color = MagneticGrey,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = quantity.text,
                    modifier =
                    Modifier
                        .semantics { contentDescription = "Current quantity" },
                    color =
                    if (quantity.isHint) {
                        MaterialTheme.colorScheme.hintColor
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    style = MaterialTheme.typography.displaySmall,
                )
                if (minQuantity is StringParam.Enable) {
                    Text(
                        modifier =
                        Modifier
                            .semantics { contentDescription = "Min allowed quantity" },
                        text =
                        stringResource(
                            Res.string.min_value,
                            minQuantity.value,
                        ),
                        color = MagneticGrey,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            IconButton(
                modifier = Modifier.semantics { contentDescription = "Button increase" },
                onClick = increase,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_plus),
                    contentDescription = "Increase",
                    tint = OrbitalBlue,
                )
            }
        }
    }
}
