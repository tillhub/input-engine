package de.tillhub.inputengine.ui.theme

import androidx.compose.material3.ButtonDefaults.buttonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ButtonElevation() = buttonElevation(
    defaultElevation = 1.dp,
    pressedElevation = 1.dp,
    disabledElevation = 1.dp,
    hoveredElevation = 1.dp,
    focusedElevation = 1.dp
)