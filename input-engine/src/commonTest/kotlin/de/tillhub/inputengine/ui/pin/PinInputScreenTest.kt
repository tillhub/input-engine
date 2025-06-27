package de.tillhub.inputengine.ui.pin

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.ui.pininput.PinInputScreen
import de.tillhub.inputengine.ui.pininput.PinInputViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PinInputScreenTest {
    private lateinit var request: PinInputRequest
    private lateinit var viewModel: PinInputViewModel

    @BeforeTest
    fun setup() {
        request = PinInputRequest(
            pin = "9876",
            extras = mapOf("argPin" to "hint for pin"),
        )
        viewModel = PinInputViewModel().apply {
            init(request.pin)
        }
    }

    @Test
    fun pinInputScreen_rendersAllComponents() = runComposeUiTest {
        var result: PinInputResult? = null
        setContent {
            PinInputScreen(
                request = request,
                viewModel = viewModel,
                onResult = {
                    result = it
                },
            )
        }

        onNodeWithContentDescription("toolbarTitle").assertTextEquals("Pin")

        onNodeWithText("9").performClick()
        onNodeWithText("8").performClick()
        onNodeWithText("7").performClick()
        onNodeWithText("6").performClick()

        waitForIdle()

        assertEquals(PinInputResult.Success(extras = mapOf("argPin" to "hint for pin")), result)
    }
}
