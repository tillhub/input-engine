package de.tillhub.inputengine.contract

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.tillhub.inputengine.ExtraKeys
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PinInputContractTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val request =
        PinInputRequest(
            pin = "1234",
            extras = mapOf("test" to "value"),
        )

    @Test
    fun inputPinLaunchCanceled() {
        var result: PinInputResult? = null
        var contract: PinInputContract? = null

        composeRule.setContent {
            val activityResultRegistry: ActivityResultRegistry =
                object : ActivityResultRegistry() {
                    override fun <I, O> onLaunch(
                        requestCode: Int,
                        contract: ActivityResultContract<I, O>,
                        input: I,
                        options: ActivityOptionsCompat?,
                    ) {
                        // Create proper ActivityResult with canceled result code
                        val activityResult = ActivityResult(Activity.RESULT_CANCELED, null)
                        @Suppress("UNCHECKED_CAST")
                        dispatchResult(requestCode, activityResult as O)
                    }
                }

            val registryOwner =
                object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry: ActivityResultRegistry
                        get() = activityResultRegistry
                }

            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                contract =
                    rememberPinInputLauncher {
                        result = it
                    }
            }
        }

        composeRule.runOnIdle {
            assertNotNull(contract)
            contract!!.launchPinInput(request)
            assertEquals(PinInputResult.Canceled, result)
        }
    }

    @Test
    fun inputPinLaunchResultOk() {
        var result: PinInputResult? = null
        var contract: PinInputContract? = null

        val successResult =
            PinInputResult.Success(
                extras = mapOf("result" to "success"),
            )

        composeRule.setContent {
            val activityResultRegistry: ActivityResultRegistry =
                object : ActivityResultRegistry() {
                    override fun <I, O> onLaunch(
                        requestCode: Int,
                        contract: ActivityResultContract<I, O>,
                        input: I,
                        options: ActivityOptionsCompat?,
                    ) {
                        // Create proper ActivityResult with success result code and serialized data
                        val resultIntent =
                            Intent().apply {
                                putExtra(ExtraKeys.EXTRAS_RESULT, Json.encodeToString(successResult))
                            }
                        val activityResult = ActivityResult(Activity.RESULT_OK, resultIntent)

                        dispatchResult(requestCode, activityResult)
                    }
                }

            val registryOwner =
                object : androidx.activity.result.ActivityResultRegistryOwner {
                    override val activityResultRegistry: ActivityResultRegistry
                        get() = activityResultRegistry
                }

            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                contract =
                    rememberPinInputLauncher {
                        result = it
                    }
            }
        }

        composeRule.runOnIdle {
            assertNotNull(contract)
            contract!!.launchPinInput(request)
            assertEquals(successResult, result)
        }
    }
}
