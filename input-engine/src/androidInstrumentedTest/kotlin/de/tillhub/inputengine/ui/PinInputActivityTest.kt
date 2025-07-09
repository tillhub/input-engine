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
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class PinInputActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun launchesWithRequestAndSubmitsResult() {
        val request = PinInputRequest(
            pin = "1234",
            overridePinInput = true,
            extras = mapOf("test" to "value"),
        )

        val intent = Intent(ApplicationProvider.getApplicationContext(), PinInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
        }

        // Launch activity for result to capture the returned data
        val scenario = ActivityScenario.launchActivityForResult<PinInputActivity>(intent)

        // Wait for UI to be ready
        Thread.sleep(500)

        // Verify UI components are displayed and interact with them
        composeRule.onNodeWithContentDescription("Override pin").assertIsDisplayed()

        // Simulate user interaction - click the override pin button to submit
        composeRule.onNodeWithContentDescription("Override pin").performClick()

        // Verify the activity result
        scenario.result.let { result ->
            assertEquals(Activity.RESULT_OK, result.resultCode)

            val resultIntent = result.resultData
            assertNotNull(resultIntent)

            val resultJson = resultIntent.getStringExtra(ExtraKeys.EXTRAS_RESULT)
            assertNotNull(resultJson)

            val pinInputResult = Json.decodeFromString<PinInputResult.Success>(resultJson)
            assertEquals(mapOf("test" to "value"), pinInputResult.extras)
        }
    }

    @Test
    fun launchesWithRequestAndCancels() {
        val request = PinInputRequest(
            pin = "1234",
            overridePinInput = false,
            extras = mapOf("test" to "value"),
        )

        val intent = Intent(ApplicationProvider.getApplicationContext(), PinInputActivity::class.java).apply {
            putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
        }

        // Launch activity for result to capture the returned data
        val scenario = ActivityScenario.launchActivityForResult<PinInputActivity>(intent)

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