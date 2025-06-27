package de.tillhub.inputengine.ui.amount

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.EUR
import de.tillhub.inputengine.financial.helper.eur
import de.tillhub.inputengine.financial.param.MoneyParam
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AmountInputScreenTest {
    private lateinit var request: AmountInputRequest
    private lateinit var viewModel: AmountInputViewModel

    @BeforeTest
    fun setup() {
        request = AmountInputRequest(
            amount = 1.eur,
            amountMin = MoneyParam.Enable(MoneyIO.of(-30_00, EUR)),
            amountMax = MoneyParam.Enable(MoneyIO.of(50_00, EUR)),
            extras = mapOf("arg" to 56)
        )
        viewModel = AmountInputViewModel().apply {
            init(request)
        }
    }

    @Test
    fun amountInputScreen_rendersAllComponents() = runComposeUiTest {
        var result: AmountInputResult? = null
        setContent {
            AmountInputScreen(
                request = request,
                viewModel = viewModel,
                onDismiss = {},
                onResult = {
                    result = it
                }
            )
        }

        onNodeWithContentDescription("toolbarTitle").assertTextEquals("Amount")
        onNodeWithText("min. -€30.00").assertIsDisplayed()
        onNodeWithText("€1.00").assertIsDisplayed()
        onNodeWithText("max. €50.00").assertIsDisplayed()

        onNodeWithText("1").performClick()
        onNodeWithText("2").performClick()
        onNodeWithText("3").performClick()
        onNodeWithText("4").performClick()

        onNodeWithText("€12.34").assertIsDisplayed()

        onNodeWithContentDescription("submitButton").performClick()
        assertEquals(AmountInputResult.Success(12.34.eur, extras = mapOf("arg" to 56)), result)
    }
}
