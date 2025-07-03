package de.tillhub.inputengine.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.runComposeUiTest

@androidx.compose.ui.test.ExperimentalTestApi
actual fun runCustomComposeUiTest(
    effectContext: kotlin.coroutines.CoroutineContext,
    size: Size,
    block: androidx.compose.ui.test.ComposeUiTest.() -> Unit
) {
    runComposeUiTest(
        effectContext = effectContext,
        block = block
    )
}