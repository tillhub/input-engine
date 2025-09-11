package de.tillhub.inputengine.theme

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val shape = Shapes(
    medium = RoundedCornerShape(16.dp),
)

@Composable
internal fun textFieldTransparentColors() = TextFieldDefaults.colors(
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)

internal val TabletScaffoldModifier =
    Modifier
        .width(380.dp)
        .fillMaxHeight()
        .padding(vertical = 24.dp)
        .clip(shape.medium)
