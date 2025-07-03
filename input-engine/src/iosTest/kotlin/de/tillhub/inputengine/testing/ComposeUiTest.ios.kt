package de.tillhub.inputengine.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runSkikoComposeUiTest
import kotlin.coroutines.CoroutineContext

@ExperimentalTestApi
actual fun runCustomComposeUiTest(
    effectContext: CoroutineContext,
    size: Size,
    block: ComposeUiTest.() -> Unit
) {
    runSkikoComposeUiTest(
        effectContext = effectContext,
        size = size,
        block = block
    )
}