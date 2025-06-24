package de.tillhub.inputengine.ui.amount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.contract.AmountInputResult
import de.tillhub.inputengine.financial.helper.eur
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.financial.param.MoneyParam
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AmountInputActivityTest {

    @Test
    fun parsesRequestAndReturnsSuccessResult() {
        val request = AmountInputRequest(
            amount = 15.00.eur,
            isZeroAllowed = true,
            amountMin = MoneyParam.Enable(10.0.eur),
            amountMax = MoneyParam.Disable,
            hintAmount = MoneyParam.Disable,
            extras = mapOf("source" to "checkout")
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            AmountInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<AmountInputActivity>(intent)

        scenario.onActivity { activity ->
            val result = AmountInputResult.Success(
                amount = 18.00.eur,
                extras = mapOf("confirmed" to "true")
            )

            activity.runOnUiThread {
                val resultIntent = Intent().apply {
                    val extras = Bundle().apply {
                        result.extras.forEach { (key, value) -> putString(key, value) }
                    }
                    val resultString = Json.encodeToString(MoneyIOSerializer, result.amount)
                    putExtra(ExtraKeys.EXTRAS_RESULT, resultString)
                    putExtra(ExtraKeys.EXTRAS_ARGS, extras)
                }
                activity.setResult(Activity.RESULT_OK, resultIntent)
                activity.finish()
            }
        }
    }

    @Test
    fun returnsCanceledResultOnDismiss() {
        val request = AmountInputRequest(amount = 0.0.eur)
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            AmountInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<AmountInputActivity>(intent)
        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.setResult(Activity.RESULT_CANCELED)
                activity.finish()
            }
        }
    }
}
