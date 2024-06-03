package de.tillhub.inputengine.ui.pininput

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.bundleOf
import androidx.test.core.app.ApplicationProvider
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.createAndroidComposeRule
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.safeResult
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PinInputActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<PinInputActivity>(
        Intent(ApplicationProvider.getApplicationContext(), PinInputActivity::class.java).apply {
            putExtra(
                ExtraKeys.EXTRA_REQUEST,
                PinInputRequest(
                    pin = "9876",
                    toolbarTitle = StringParam.String("PIN title"),
                    extras = bundleOf("argPin" to "hint for pin")
                )
            )
        }
    )

    @Test
    fun init() {
        with(composeRule) {
            onNodeWithTag("toolbarTitle").assertTextEquals("PIN title")

            onNodeWithText("9").performClick()
            onNodeWithText("8").performClick()
            onNodeWithText("7").performClick()
            onNodeWithText("6").performClick()

            waitForIdle()

            val resultCode = composeRule.activityRule.scenario.safeResult.resultCode
            val resultData = composeRule.activityRule.scenario.safeResult.resultData

            assertEquals(Activity.RESULT_OK, resultCode)
            assertTrue(resultData.hasExtra("argPin"))
            assertEquals(resultData?.getStringExtra("argPin"), "hint for pin")
        }
    }
}