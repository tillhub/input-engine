package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PinInputContractTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSuccessResultParsing() {
        val extras = Bundle().apply {
            putString("pinDigits", "1234")
            putString("entrySource", "keypad")
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, Intent().putExtras(extras))
        lateinit var result: PinInputResult

        composeTestRule.setContent {
            rememberPinInputLauncher {
                result = it
            }.apply {
                result = parsePinInputResult(activityResult.resultCode, activityResult.data?.extras)
            }
        }

        composeTestRule.runOnIdle {
            val expected = PinInputResult.Success(
                mapOf("pinDigits" to "1234", "entrySource" to "keypad"),
            )
            assertEquals(expected, result)
        }
    }

    @Test
    fun testCanceledResult() {
        val activityResult = ActivityResult(Activity.RESULT_CANCELED, Intent())
        lateinit var result: PinInputResult

        composeTestRule.setContent {
            rememberPinInputLauncher {
                result = it
            }.apply {
                result = parsePinInputResult(activityResult.resultCode, activityResult.data?.extras)
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(PinInputResult.Canceled, result)
        }
    }
}
