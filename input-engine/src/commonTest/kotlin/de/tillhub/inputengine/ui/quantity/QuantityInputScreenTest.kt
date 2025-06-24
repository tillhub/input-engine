package de.tillhub.inputengine.ui.quantity

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.param.QuantityParam
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class QuantityInputScreenTest {
    private lateinit var request: QuantityInputRequest
    private lateinit var viewModel: QuantityInputViewModel

    @BeforeTest
    fun setup() {
        request = QuantityInputRequest(
            quantity = QuantityIO.ZERO,
            quantityHint = QuantityParam.Enable(QuantityIO.of(5)),
            minQuantity = QuantityParam.Enable(QuantityIO.of(1)),
            maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
            extras = mapOf("argQty" to 73)
        )

        viewModel = QuantityInputViewModel().apply {
            setInitialValue(request)
        }
    }

    @Test
    fun quantityInputScreen_rendersAllComponents() = runComposeUiTest {
        var result: QuantityInputResult? = null
        setContent {
            QuantityInputScreen(
                request = request,
                viewModel = viewModel,
                onResult = {
                    result = it
                }
            )
        }

        onNodeWithTag("toolbarTitle").assertTextEquals("Quantity")
        onNodeWithText("min. 1").assertIsDisplayed()
        onNodeWithTag("qtyValue").assertTextEquals("5")
        onNodeWithText("max. 20").assertIsDisplayed()

        onNodeWithText("1").performClick()
        onNodeWithText("6").performClick()

        onNodeWithTag("qtyValue").assertTextEquals("16")

        onNodeWithTag("submitButton").performClick()
        assertEquals(
            QuantityInputResult.Success(QuantityIO.of(16), extras = mapOf("argQty" to 73)),
            result
        )
    }
}
