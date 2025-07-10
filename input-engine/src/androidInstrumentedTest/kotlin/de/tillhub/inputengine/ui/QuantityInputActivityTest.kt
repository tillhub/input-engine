package de.tillhub.inputengine.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class QuantityInputActivityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun launchesWithRequestAndSubmitsResult() {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.of(5),
                allowsZero = true,
                extras = mapOf("test" to "value"),
            )

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), QuantityInputActivity::class.java).apply {
                putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
            }

        // Launch activity for result to capture the returned data
        val scenario = ActivityScenario.launchActivityForResult<QuantityInputActivity>(intent)

        // Wait for UI to be ready
        Thread.sleep(500)

        // Verify UI components are displayed and interact with them
        composeRule.onNodeWithContentDescription("Submit button label").assertIsDisplayed()

        // Simulate user interaction - click the submit button
        composeRule.onNodeWithContentDescription("Submit button label").performClick()

        // Verify the activity result
        scenario.result.let { result ->
            assertEquals(Activity.RESULT_OK, result.resultCode)

            val resultIntent = result.resultData
            assertNotNull(resultIntent)

            val resultJson = resultIntent.getStringExtra(ExtraKeys.EXTRAS_RESULT)
            assertNotNull(resultJson)

            val quantityInputResult = Json.decodeFromString<QuantityInputResult.Success>(resultJson)
            assertEquals(QuantityIO.of(5), quantityInputResult.quantity)
            assertEquals(mapOf("test" to "value"), quantityInputResult.extras)
        }
    }

    @Test
    fun launchesWithRequestAndCancels() {
        val request =
            QuantityInputRequest(
                quantity = QuantityIO.of(10),
                allowsZero = false,
                minQuantity = QuantityParam.Enable(QuantityIO.of(1)),
                maxQuantity = QuantityParam.Enable(QuantityIO.of(100)),
                extras = mapOf("test" to "value", "complex" to "request"),
            )

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), QuantityInputActivity::class.java).apply {
                putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
            }

        // Launch activity for result to capture the returned data
        val scenario = ActivityScenario.launchActivityForResult<QuantityInputActivity>(intent)

        // Wait for UI to be ready
        Thread.sleep(500)

        // Verify toolbar back button is displayed
        composeRule.onNodeWithContentDescription("Toolbar back button").assertIsDisplayed()

        // Simulate user dismissing the screen by clicking the back button
        composeRule.onNodeWithContentDescription("Toolbar back button").performClick()

        // Activity finishes immediately after dismiss, so capture result right away
        scenario.result.let { result ->
            assertEquals(Activity.RESULT_CANCELED, result.resultCode)

            // When canceled, there should be no result data
            val resultIntent = result.resultData
            assertNull(resultIntent)
        }
    }
}
