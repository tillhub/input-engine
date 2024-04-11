package de.tillhub.inputengine.ui.theme

import androidx.compose.material3.ButtonDefaults.buttonElevation
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun buttonElevation() = buttonElevation(
    defaultElevation = 1.dp,
    pressedElevation = 1.dp,
    disabledElevation = 1.dp,
    hoveredElevation = 1.dp,
    focusedElevation = 1.dp
)

@Composable
fun textFieldTransparentColors() = TextFieldDefaults.colors(
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)
