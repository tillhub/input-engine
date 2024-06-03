package de.tillhub.inputengine.ui.quantity

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
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.createAndroidComposeRule
import de.tillhub.inputengine.data.ExtraKeys
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.StringParam
import de.tillhub.inputengine.safeResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class QuantityInputActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<QuantityInputActivity>(
        Intent(ApplicationProvider.getApplicationContext(), QuantityInputActivity::class.java).apply {
            putExtra(
                ExtraKeys.EXTRA_REQUEST,
                QuantityInputRequest(
                    quantity = QuantityIO.ZERO,
                    quantityHint = QuantityParam.Enable(QuantityIO.of(5)),
                    minQuantity = QuantityParam.Enable(QuantityIO.of(1)),
                    maxQuantity = QuantityParam.Enable(QuantityIO.of(20)),
                    toolbarTitle = StringParam.String("Qty title"),
                    extras = bundleOf("argQty" to 73)
                )
            )
        }
    )

    @Test
    fun init() {
        with(composeRule) {
            onNodeWithTag("toolbarTitle").assertTextEquals("Qty title")
            onNodeWithText("min. 1").assertIsDisplayed()
            onNodeWithTag("qtyValue").assertTextEquals("5")
            onNodeWithText("max. 20").assertIsDisplayed()

            onNodeWithText("1").performClick()
            onNodeWithText("6").performClick()

            onNodeWithTag("qtyValue").assertTextEquals("16")

            onNodeWithTag("submitButton").performClick()

            val resultCode = composeRule.activityRule.scenario.safeResult.resultCode
            val resultData = composeRule.activityRule.scenario.safeResult.resultData
            val inputPercent = BundleCompat.getSerializable(resultData.extras!!, ExtraKeys.EXTRAS_RESULT, QuantityIO::class.java)
            val inputArgs = resultData.extras!!.getBundle(ExtraKeys.EXTRAS_ARGS)

            assertEquals(Activity.RESULT_OK, resultCode)
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_RESULT))
            assertTrue(resultData.hasExtra(ExtraKeys.EXTRAS_ARGS))
            assertEquals(inputPercent, QuantityIO.of(16))
            assertEquals(inputArgs?.size(), 1)
            assertEquals(inputArgs?.getInt("argQty"), 73)
        }
    }
}