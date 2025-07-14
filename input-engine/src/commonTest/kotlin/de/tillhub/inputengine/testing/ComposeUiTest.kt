package de.tillhub.inputengine.testing

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalTestApi
expect fun runCustomComposeUiTest(
    effectContext: CoroutineContext = EmptyCoroutineContext,
    size: Size = Size(1024.0f, 768.0f),
    block: ComposeUiTest.() -> Unit,
)
