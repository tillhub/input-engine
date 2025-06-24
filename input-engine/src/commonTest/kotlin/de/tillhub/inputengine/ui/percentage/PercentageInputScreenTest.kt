package de.tillhub.inputengine.ui.percentage

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PercentageInputScreenTest {
    private lateinit var request: PercentageInputRequest
    private lateinit var viewModel: PercentageInputViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        request = PercentageInputRequest(
            percent = PercentIO.of(15),
            percentageMin = PercentageParam.Enable(PercentIO.of(5)),
            percentageMax = PercentageParam.Enable(PercentIO.WHOLE),
            extras = mapOf("argPercentage" to 6)
        )
        viewModel = PercentageInputViewModel().apply {
            init(request)
        }
    }

    @Test
    fun percentageInputScreen_rendersAllComponents() = runComposeUiTest {
        var result: PercentageInputResult? = null
        setContent {
            PercentageInputScreen(
                request = request,
                viewModel = viewModel,
                onDismiss = {},
                onResult = {
                    result = it
                }
            )
        }

        onNodeWithTag("toolbarTitle").assertTextEquals("Percentage")
        onNodeWithText("min. 5%").assertIsDisplayed()
        onNodeWithTag("percentValue").printToLog("percentValue")
        onNodeWithTag("percentValue").assertTextEquals("15%")
        onNodeWithText("max. 100%").assertIsDisplayed()

        onNodeWithText("2").performClick()
        onNodeWithText("5").performClick()
        onNodeWithTag("percentValue").printToLog("percentValue")
        onNodeWithTag("percentValue").assertTextEquals("25%")

        onNodeWithTag("submitButton").performClick()

        assertEquals(
            PercentageInputResult.Success(
                PercentIO.of(25),
                extras = mapOf("argPercentage" to 6)
            ), result
        )
    }
}
