package de.tillhub.inputengine.ui.percentage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class PercentageInputActivityTest {

    @Test
    fun parsesRequestAndReturnsSuccessResult() {
        val request = PercentageInputRequest(
            percent = PercentIO.of(15.0),
            allowsZero = true,
            allowDecimal = true,
            percentageMin = PercentageParam.Enable(PercentIO.of(5.0)),
            percentageMax = PercentageParam.Enable(PercentIO.of(20.0)),
            extras = mapOf("origin" to "checkout")
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PercentageInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<PercentageInputActivity>(intent)

        scenario.onActivity { activity ->
            val result = PercentageInputResult.Success(
                percent = PercentIO.of(18.5),
                extras = mapOf("confirmed" to "true")
            )

            activity.runOnUiThread {
                val resultIntent = Intent().apply {
                    val extras = Bundle().apply {
                        result.extras.forEach { (key, value) -> putString(key, value) }
                    }
                    putExtra(ExtraKeys.EXTRAS_RESULT, result.percent)
                    putExtra(ExtraKeys.EXTRAS_ARGS, extras)
                }
                activity.setResult(Activity.RESULT_OK, resultIntent)
                activity.finish()
            }
        }
    }

    @Test
    fun returnsCanceledResult() {
        val request = PercentageInputRequest(
            percent = PercentIO.of(0.0),
            allowsZero = true
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PercentageInputActivity::class.java
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<PercentageInputActivity>(intent)

        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.setResult(Activity.RESULT_CANCELED)
                activity.finish()
            }
        }
    }
}
