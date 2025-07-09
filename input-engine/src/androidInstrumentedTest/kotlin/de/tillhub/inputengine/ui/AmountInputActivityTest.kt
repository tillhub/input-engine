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
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.eur
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class AmountInputActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun launchesWithRequestAndSubmitsResult() {
        val request = AmountInputRequest(
            amount = 15.00.eur,
            isZeroAllowed = true,
            amountMin = MoneyParam.Enable(10.0.eur),
            amountMax = MoneyParam.Disable,
            hintAmount = MoneyParam.Disable,
            extras = mapOf("source" to "42"),
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            AmountInputActivity::class.java,
        ).apply {
            putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
        }

        // Launch activity for result to capture the returned data
        val scenario = ActivityScenario.launchActivityForResult<AmountInputActivity>(intent)

        // Wait for UI to be ready
        Thread.sleep(500)

        // Verify UI components are displayed and interact with them
        composeRule.onNodeWithContentDescription("Submit button label").assertIsDisplayed()

        // Simulate user interaction - click the submit button
        composeRule.onNodeWithContentDescription("Submit button label").performClick()

        // Wait for the result to be processed
        Thread.sleep(500)

        // Verify the activity result
        scenario.result.let { result ->
            assertEquals(Activity.RESULT_OK, result.resultCode)
            
            val resultIntent = result.resultData
            assertNotNull(resultIntent)
            
            val resultJson = resultIntent.getStringExtra(ExtraKeys.EXTRAS_RESULT)
            assertNotNull(resultJson)
            
            val amountInputResult = Json.decodeFromString<AmountInputResult.Success>(resultJson)
            assertEquals(15.00.eur, amountInputResult.amount)
            assertEquals(mapOf("source" to "42"), amountInputResult.extras)
        }
    }

    @Test
    fun launchesWithRequestAndCancels() {
        val request = AmountInputRequest(
            amount = 25.50.eur,
            isZeroAllowed = false,
            amountMin = MoneyParam.Enable(1.0.eur),
            amountMax = MoneyParam.Enable(100.0.eur),
            hintAmount = MoneyParam.Enable(20.0.eur),
            extras = mapOf("test" to "value", "id" to "123"),
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            AmountInputActivity::class.java,
        ).apply {
            putExtra(ExtraKeys.EXTRAS_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launchActivityForResult<AmountInputActivity>(intent)

        Thread.sleep(500)

        // Verify min amount is displayed when enabled
        composeRule.onNodeWithContentDescription("Toolbar back button").assertIsDisplayed()

        // Simulate user dismissing the screen by clicking the back button
        composeRule.onNodeWithContentDescription("Toolbar back button").performClick()

        // Activity finishes immediately after dismiss, so capture result right away
        // Verify the activity result is CANCELED (not success)
        scenario.result.let { result ->
            assertEquals(Activity.RESULT_CANCELED, result.resultCode)
            
            // When canceled, there should be no result data
            val resultIntent = result.resultData
            assertNull(resultIntent)
        }
    }
}