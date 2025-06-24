package de.tillhub.inputengine.ui.quantity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.contract.QuantityInputResult
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.param.QuantityParam
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class QuantityInputActivityTest {

    @Test
    fun parsesRequestAndReturnsSuccessResult() {
        val request = QuantityInputRequest(
            quantity = QuantityIO.of(2.0),
            allowsZero = false,
            allowDecimal = true,
            minQuantity = QuantityParam.Enable(QuantityIO.of(1.0)),
            maxQuantity = QuantityParam.Enable(QuantityIO.of(10.0)),
            extras = mapOf("usage" to "refill")
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuantityInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<QuantityInputActivity>(intent)

        scenario.onActivity { activity ->
            val result = QuantityInputResult.Success(
                quantity = QuantityIO.of(5.5),
                extras = mapOf("confirmed" to "true")
            )

            activity.runOnUiThread {
                val resultIntent = Intent().apply {
                    putExtra(ExtraKeys.EXTRAS_RESULT, result.quantity.toDouble())
                    putExtra(
                        ExtraKeys.EXTRAS_ARGS,
                        Bundle().apply {
                            result.extras.forEach { (key, value) -> putString(key, value) }
                        }
                    )
                }
                activity.setResult(Activity.RESULT_OK, resultIntent)
                activity.finish()
            }
        }
    }

    @Test
    fun returnsCanceledResult() {
        val request = QuantityInputRequest(
            quantity = QuantityIO.of(0.0),
            allowsZero = true,
            allowDecimal = false
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            QuantityInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<QuantityInputActivity>(intent)

        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.setResult(Activity.RESULT_CANCELED)
                activity.finish()
            }
        }
    }
}
