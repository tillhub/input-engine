package de.tillhub.inputengine.test.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.contract.rememberPercentageInputLauncher
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
        val extras = Bundle().apply {
            putString("source", "user-typed")
        }

        val intent = Intent().apply {
            putExtra(ExtraKeys.EXTRAS_RESULT, percentValue)
            putExtra(ExtraKeys.EXTRAS_ARGS, extras)
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, intent)

        composeTestRule.setContent {
            rememberPercentageInputLauncher {
                result = it
            }.apply {
                // Simulate parsing logic
                val resultData = activityResult.data?.extras
                val parsedPercent = resultData?.getDouble(ExtraKeys.EXTRAS_RESULT)
                val parsedExtras = resultData?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key -> resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key).orEmpty()  }
                    .orEmpty()

                result = PercentageInputResult.Success(
                    PercentIO.of(checkNotNull(parsedPercent)), parsedExtras
                )
            }
        }

        composeTestRule.runOnIdle {
            val expected = PercentageInputResult.Success(
                PercentIO.of(percentValue),
                mapOf("source" to "user-typed")
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
                val resultData = activityResult.data?.extras
                val percentValue = resultData?.getDouble(ExtraKeys.EXTRAS_RESULT)
                val extras = resultData
                    ?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key -> resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key).orEmpty() }
                    .orEmpty()

                result = if (activityResult.resultCode == Activity.RESULT_OK && percentValue != null) {
                    PercentageInputResult.Success(
                        PercentIO.of(percentValue),
                        extras
                    )
                } else {
                    PercentageInputResult.Canceled
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(PercentageInputResult.Canceled, result)
        }
    }
}
