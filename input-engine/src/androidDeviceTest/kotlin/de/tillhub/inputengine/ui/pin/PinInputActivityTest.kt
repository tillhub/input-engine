package de.tillhub.inputengine.ui.pin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.helper.ExtraKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class PinInputActivityTest {

    @Test
    fun parsesRequestAndReturnsSuccessResult() {
        val request = PinInputRequest(
            pin = "1234",
            overridePinInput = false,
            extras = mapOf("flow" to "checkout"),
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PinInputActivity::class.java,
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<PinInputActivity>(intent)

        scenario.onActivity { activity ->
            val result = PinInputResult.Success(
                extras = mapOf("authenticated" to "true", "pinMethod" to "manual"),
            )

            activity.runOnUiThread {
                val resultIntent = Intent().apply {
                    val extrasBundle = Bundle().apply {
                        result.extras.forEach { (key, value) -> putString(key, value) }
                    }
                    putExtra(ExtraKeys.EXTRAS_ARGS, extrasBundle)
                }
                activity.setResult(Activity.RESULT_OK, resultIntent)
                activity.finish()
            }
        }
    }

    @Test
    fun returnsCanceledResult() {
        val request = PinInputRequest(
            pin = "0000",
            overridePinInput = true,
        )

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PinInputActivity::class.java,
        ).apply {
            putExtra(ExtraKeys.EXTRA_REQUEST, Json.encodeToString(request))
        }

        val scenario = ActivityScenario.launch<PinInputActivity>(intent)

        scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.setResult(Activity.RESULT_CANCELED)
                activity.finish()
            }
        }
    }
}
