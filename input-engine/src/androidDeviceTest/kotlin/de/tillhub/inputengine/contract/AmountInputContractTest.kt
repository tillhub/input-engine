package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.financial.helper.eur
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AmountInputContractTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun verify_success_result_parsing() {
        val testAmount = 123.45.eur
        val extrasBundle = Bundle().apply {
            putInt("comment", 1)
        }

        val intent = Intent().apply {
            putExtra(
                ExtraKeys.EXTRAS_RESULT,
                Json.encodeToString(MoneyIOSerializer, testAmount),
            )
            putExtra(ExtraKeys.EXTRAS_ARGS, extrasBundle)
        }

        val activityResult = ActivityResult(Activity.RESULT_OK, intent)

        lateinit var capturedResult: AmountInputResult

        composeRule.setContent {
            rememberAmountInputLauncher {
                capturedResult = it
            }.apply {
                capturedResult =
                    parseAmountInputResult(activityResult.resultCode, activityResult.data?.extras)
            }
        }

        composeRule.runOnIdle {
            val expected = AmountInputResult.Success(
                amount = testAmount,
                extras = mapOf("comment" to 1),
            )
            assertEquals(expected, capturedResult)
        }
    }

    @Test
    fun verify_canceled_result() {
        val resultCode = Activity.RESULT_CANCELED
        val activityResult = ActivityResult(resultCode, Intent())

        lateinit var capturedResult: AmountInputResult

        composeRule.setContent {
            rememberAmountInputLauncher {
                capturedResult = it
            }.apply {
                capturedResult =
                    parseAmountInputResult(activityResult.resultCode, activityResult.data?.extras)
            }
        }

        composeRule.runOnIdle {
            assertEquals(AmountInputResult.Canceled, capturedResult)
        }
    }
}
