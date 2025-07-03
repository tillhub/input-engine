package de.tillhub.inputengine.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.coroutines.CoroutineContext

@ExperimentalTestApi
actual fun runCustomComposeUiTest(
    effectContext: CoroutineContext,
    size: Size,
    block: ComposeUiTest.() -> Unit
) {
    runComposeUiTest(
        effectContext = effectContext,
        block = block
    )
}