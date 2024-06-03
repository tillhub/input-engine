package de.tillhub.inputengine.ui.percentage

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.test.core.app.ApplicationProvider
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.createAndroidComposeRule
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.safeResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PercentageInputActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<PercentageInputActivity>(
        Intent(ApplicationProvider.getApplicationContext(), PercentageInputActivity::class.java).apply {
            putExtra(
                ExtraKeys.EXTRA_REQUEST,
                PercentageInputRequest(
                    percent = PercentIO.of(15),
                    toolbarTitle = StringParam.String("Percentage title"),
                    percentageMin = PercentageParam.Enable(PercentIO.of(5)),
                    percentageMax = PercentageParam.Enable(PercentIO.WHOLE),
                    extras = bundleOf("argPercentage" to 6)
                )
            )
        }
    )

    @Test
    fun init() {
        with(composeRule) {
            onNodeWithTag("toolbarTitle").assertTextEquals("Percentage title")
            onNodeWithText("min. 5%").assertIsDisplayed()
            onNodeWithTag("percentValue").assertTextEquals("15%")
            onNodeWithText("max. 100%").assertIsDisplayed()

            onNodeWithText("2").performClick()
            onNodeWithText("5").performClick()

            onNodeWithTag("percentValue").assertTextEquals("25%")

            onNodeWithTag("submitButton").performClick()

            val resultCode = composeRule.activityRule.scenario.safeResult.resultCode
            val resultData = composeRule.activityRule.scenario.safeResult.resultData
            val inputPercent = BundleCompat.getSerializable(resultData.extras!!, ExtraKeys.EXTRAS_RESULT, PercentIO::class.java)
            val inputArgs = resultData.extras!!.getBundle(ExtraKeys.EXTRAS_ARGS)

            assertEquals(Activity.RESULT_OK, resultCode)
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_RESULT))
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_ARGS))
            assertEquals(inputPercent, PercentIO.of(25))
            assertEquals(inputArgs?.size(), 1)
            assertEquals(inputArgs?.getInt("argPercentage"), 6)
        }
    }
}