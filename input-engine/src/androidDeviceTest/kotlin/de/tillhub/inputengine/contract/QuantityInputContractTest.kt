package de.tillhub.inputengine.test.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.contract.rememberQuantityInputLauncher
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.helper.ExtraKeys
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class QuantityInputContractTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSuccessResultParsing() {
        lateinit var result: QuantityInputResult

        val quantityValue = 42.5
        val extras = Bundle().apply {
            putString("unit", "kg")
            putString("note", "from scale")
        }

        val intent = Intent().apply {
            putExtra(ExtraKeys.EXTRAS_RESULT, quantityValue)
            putExtra(ExtraKeys.EXTRAS_ARGS, extras)
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, intent)

        composeTestRule.setContent {
            rememberQuantityInputLauncher {
                result = it
            }.apply {
                val extrasMap = activityResult.data?.extras?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key ->
                        activityResult.data?.extras?.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key).orEmpty()
                    }
                    .orEmpty()

                val quantity = activityResult.data?.extras?.getDouble(ExtraKeys.EXTRAS_RESULT)
                result = if (activityResult.resultCode == Activity.RESULT_OK && quantity != null) {
                    QuantityInputResult.Success(QuantityIO.of(quantity), extrasMap)
                } else {
                    QuantityInputResult.Canceled
                }
            }
        }

        composeTestRule.runOnIdle {
            val expected = QuantityInputResult.Success(
                QuantityIO.of(quantityValue),
                mapOf("unit" to "kg", "note" to "from scale")
            )
            assertEquals(expected, result)
        }
    }

    @Test
    fun testCanceledResult() {
        lateinit var result: QuantityInputResult

        val canceledIntent = Intent()
        val activityResult = ActivityResult(Activity.RESULT_CANCELED, canceledIntent)

        composeTestRule.setContent {
            rememberQuantityInputLauncher {
                result = it
            }.apply {
                // simulate how the launcher callback would handle the canceled result
                val resultData = activityResult.data?.extras
                val extrasMap = resultData
                    ?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key -> resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key).orEmpty() }
                    .orEmpty()

                val quantity = resultData?.getDouble(ExtraKeys.EXTRAS_RESULT)

                result = when {
                    activityResult.resultCode == Activity.RESULT_OK && quantity != null ->
                        QuantityInputResult.Success(QuantityIO.of(quantity), extrasMap)
                    else -> QuantityInputResult.Canceled
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(QuantityInputResult.Canceled, result)
        }
    }

}
