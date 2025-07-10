package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import de.tillhub.inputengine.ExtraKeys
import de.tillhub.inputengine.data.PercentIO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PercentageInputContractTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun inputPercentageLaunchCanceled() {
        var result: PercentageInputResult? = null
        var contract: PercentageInputContract? = null

        composeRule.setContent {
            val activityResultRegistry: ActivityResultRegistry = object : ActivityResultRegistry() {
                override fun <I, O> onLaunch(
                    requestCode: Int,
                    contract: ActivityResultContract<I, O>,
                    input: I,
                    options: ActivityOptionsCompat?,
                ) {
                    // Simulate a canceled result (Activity.RESULT_CANCELED)
                    val activityResult = ActivityResult(Activity.RESULT_CANCELED, null)

                    dispatchResult(requestCode, activityResult)
                }
            }

            val registryOwner = object : ActivityResultRegistryOwner {
                override val activityResultRegistry: ActivityResultRegistry
                    get() = activityResultRegistry
            }

            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                contract = rememberPercentageInputLauncher {
                    result = it
                }
            }
        }

        composeRule.runOnIdle {
            assertNotNull(contract, "PercentageInputContract should be created successfully")

            // Test that the contract can be used to launch a request
            contract.launchPercentageInput(
                PercentageInputRequest(),
            )

            // Verify the result callback was invoked with Canceled result
            assertEquals(PercentageInputResult.Canceled, result, "Result callback should be invoked with Canceled result")
        }
    }

    @Test
    fun inputPercentageLaunchResultOk() {
        var result: PercentageInputResult? = null
        var contract: PercentageInputContract? = null

        composeRule.setContent {
            val activityResultRegistry: ActivityResultRegistry = object : ActivityResultRegistry() {
                override fun <I, O> onLaunch(
                    requestCode: Int,
                    contract: ActivityResultContract<I, O>,
                    input: I,
                    options: ActivityOptionsCompat?,
                ) {
                    val response = PercentageInputResult.Success(
                        percent = PercentIO.of(10),
                        extras = mapOf("test" to "value"),
                    )
                    val data = Intent().apply {
                        putExtra(ExtraKeys.EXTRAS_RESULT, Json.encodeToString(response))
                    }
                    val activityResult = ActivityResult(Activity.RESULT_OK, data)

                    dispatchResult(requestCode, activityResult)
                }
            }

            val registryOwner = object : ActivityResultRegistryOwner {
                override val activityResultRegistry: ActivityResultRegistry
                    get() = activityResultRegistry
            }

            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                contract = rememberPercentageInputLauncher {
                    result = it
                }
            }
        }

        composeRule.runOnIdle {
            assertNotNull(contract, "PercentageInputContract should be created successfully")

            // Test that the contract can be used to launch a request
            contract.launchPercentageInput(
                PercentageInputRequest(),
            )

            // Verify the result callback was invoked with Canceled result
            assertEquals(
                PercentageInputResult.Success(
                    percent = PercentIO.of(10),
                    extras = mapOf("test" to "value"),
                ),
                result,
                "Result callback should be invoked with Success result",
            )
        }
    }
}
