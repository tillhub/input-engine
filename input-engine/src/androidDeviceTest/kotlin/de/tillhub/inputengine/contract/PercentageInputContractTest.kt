package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.helper.ExtraKeys
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PercentageInputContractTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSuccessResultParsing() {
        lateinit var result: PercentageInputResult

        val percentValue = 17.5
        val extrasBundle = Bundle().apply {
            putInt("source_id", 42)
        }

        val intent = Intent().apply {
            putExtra(ExtraKeys.EXTRAS_RESULT, percentValue)
            putExtra(ExtraKeys.EXTRAS_ARGS, extrasBundle)
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, intent)

        composeTestRule.setContent {
            rememberPercentageInputLauncher {
                result = it
            }.apply {
                val parsed = parsePercentageInputResult(activityResult.resultCode, activityResult.data?.extras)
                result = parsed
            }
        }

        composeTestRule.runOnIdle {
            val expected = PercentageInputResult.Success(
                percent = PercentIO.of(percentValue),
                extras = mapOf("source_id" to 42),
            )
            assertEquals(expected, result)
        }
    }

    @Test
    fun testCanceledResult() {
        lateinit var result: PercentageInputResult

        val canceledIntent = Intent()
        val activityResult = ActivityResult(Activity.RESULT_CANCELED, canceledIntent)

        composeTestRule.setContent {
            rememberPercentageInputLauncher {
                result = it
            }.apply {
                result = parsePercentageInputResult(activityResult.resultCode, activityResult.data?.extras)
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(PercentageInputResult.Canceled, result)
        }
    }
}
