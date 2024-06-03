package de.tillhub.inputengine.ui.moneyinput

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
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.createAndroidComposeRule
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.helper.EUR
import de.tillhub.inputengine.safeResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MoneyInputActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MoneyInputActivity>(
        Intent(ApplicationProvider.getApplicationContext(), MoneyInputActivity::class.java).apply {
            putExtra(
                ExtraKeys.EXTRA_REQUEST,
                AmountInputRequest(
                    amount = MoneyIO.of(100, EUR),
                    toolbarTitle = StringParam.String("Toolbar title"),
                    amountMin = MoneyParam.Enable(MoneyIO.of(-30_00, EUR)),
                    amountMax = MoneyParam.Enable(MoneyIO.of(50_00, EUR)),
                    extras = bundleOf("arg" to 56)
                )
            )
        }
    )

    @Test
    fun init() {
        with(composeRule) {
            onNodeWithTag("toolbarTitle").assertTextEquals("Toolbar title")
            onNodeWithText("min. -€30.00").assertIsDisplayed()
            onNodeWithText("€1.00").assertIsDisplayed()
            onNodeWithText("max. €50.00").assertIsDisplayed()

            onNodeWithText("1").performClick()
            onNodeWithText("2").performClick()
            onNodeWithText("3").performClick()
            onNodeWithText("4").performClick()

            onNodeWithText("€12.34").assertIsDisplayed()

            onNodeWithTag("submitButton").performClick()

            val resultCode = composeRule.activityRule.scenario.safeResult.resultCode
            val resultData = composeRule.activityRule.scenario.safeResult.resultData
            val inputAmount = BundleCompat.getSerializable(resultData.extras!!, ExtraKeys.EXTRAS_RESULT, MoneyIO::class.java)
            val inputArgs = resultData.extras!!.getBundle(ExtraKeys.EXTRAS_ARGS)

            assertEquals(Activity.RESULT_OK, resultCode)
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_RESULT))
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_ARGS))
            assertEquals(inputAmount, MoneyIO.of(12_34, EUR))
            assertEquals(inputArgs?.size(), 1)
            assertEquals(inputArgs?.getInt("arg"), 56)
        }
    }
}