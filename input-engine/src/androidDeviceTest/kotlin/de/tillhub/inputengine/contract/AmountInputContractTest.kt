package de.tillhub.inputengine.test.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.contract.rememberAmountInputLauncher
import de.tillhub.inputengine.financial.helper.eur
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class AmountInputContractTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun verify_success_result_parsing() {
        lateinit var capturedResult: AmountInputResult

        val testAmount = 123.45.eur
        val extrasBundle = Bundle().apply {
            putString("comment", "paid in cash")
        }

        val intent = Intent().apply {
            putExtra(ExtraKeys.EXTRAS_RESULT, Json.Default.encodeToString(MoneyIOSerializer, testAmount))
            putExtra(ExtraKeys.EXTRAS_ARGS, extrasBundle)
        }

        val result = Activity.RESULT_OK
        val activityResult = ActivityResult(result, intent)

        composeRule.setContent {
            rememberAmountInputLauncher {
                capturedResult = it
            }.apply {
                // simulate handling the result as the launcher would do internally
                val data = activityResult.data?.extras
                val amount = data?.getString(ExtraKeys.EXTRAS_RESULT)?.let {
                    Json.Default.decodeFromString(MoneyIOSerializer, it)
                }

                val extras = data
                    ?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key -> data.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key)!! }
                    ?: emptyMap()

                capturedResult = AmountInputResult.Success(checkNotNull(amount), extras)
            }
        }

        composeRule.runOnIdle {
            val expected = AmountInputResult.Success(testAmount, mapOf("comment" to "paid in cash"))
            Assert.assertEquals(expected, capturedResult)
        }
    }

    @Test
    fun verify_canceled_result() {
        lateinit var capturedResult: AmountInputResult

        val resultCode = Activity.RESULT_CANCELED
        val intent = Intent() // empty intent
        val activityResult = ActivityResult(resultCode, intent)

        composeRule.setContent {
            rememberAmountInputLauncher {
                capturedResult = it
            }.apply {
                val resultData = activityResult.data?.extras
                val amountJson = resultData?.getString(ExtraKeys.EXTRAS_RESULT)
                val amount = amountJson?.let { json -> Json.Default.decodeFromString(
                    MoneyIOSerializer, json) }

                val extrasMap = resultData
                    ?.getBundle(ExtraKeys.EXTRAS_ARGS)
                    ?.keySet()
                    ?.associateWith { key ->
                        resultData.getBundle(ExtraKeys.EXTRAS_ARGS)?.getString(key)
                            ?: throw IllegalArgumentException("Non-string value for key: $key")
                    }.orEmpty()

                capturedResult = if (activityResult.resultCode == Activity.RESULT_OK && amount != null) {
                    AmountInputResult.Success(amount, extrasMap)
                } else {
                    AmountInputResult.Canceled
                }
            }
        }

        composeRule.runOnIdle {
            Assert.assertEquals(AmountInputResult.Canceled, capturedResult)
        }
    }
}