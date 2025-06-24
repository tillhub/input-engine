package de.tillhub.inputengine.test.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.contract.rememberPinInputLauncher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PinInputContractTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSuccessResultParsing() {
        lateinit var result: PinInputResult

        val extras = Bundle().apply {
            putString("pinDigits", "1234")
            putString("entrySource", "keypad")
        }

        val intent = Intent().apply {
            putExtras(extras)
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, intent)

        composeTestRule.setContent {
            rememberPinInputLauncher {
                result = it
            }.apply {
                val parsedExtras = activityResult.data?.extras
                val parsedMap = parsedExtras?.keySet()
                    ?.associateWith { key -> parsedExtras.getString(key).orEmpty() }
                    .orEmpty()
                result = PinInputResult.Success(parsedMap)
            }
        }

        composeTestRule.runOnIdle {
            val expected = PinInputResult.Success(
                mapOf("pinDigits" to "1234", "entrySource" to "keypad")
            )
            assertEquals(expected, result)
        }
    }

    @Test
    fun testCanceledResult() {
        lateinit var result: PinInputResult

        val canceledIntent = Intent()
        val activityResult = ActivityResult(Activity.RESULT_CANCELED, canceledIntent)

        composeTestRule.setContent {
            rememberPinInputLauncher {
                result = it
            }.apply {
                val extras = activityResult.data?.extras
                result = if (activityResult.resultCode == Activity.RESULT_OK && extras != null) {
                    val map = extras.keySet().associateWith { extras.getString(it).orEmpty() }
                    PinInputResult.Success(map)
                } else {
                    PinInputResult.Canceled
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(PinInputResult.Canceled, result)
        }
    }

}
