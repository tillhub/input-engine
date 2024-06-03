package de.tillhub.inputengine.ui.components

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import de.tillhub.inputengine.R
import de.tillhub.inputengine.assertColor
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class SubmitButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }

    interface ButtonEvents {
        fun onClick()
    }

    @Test
    fun testEnabledSubmitButton() {

        val events: ButtonEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                SubmitButton(
                    isEnable = true,
                    onClick = events::onClick
                )
            }

            onNodeWithTag("submitButton").assertIsEnabled()
            onNodeWithTag("submitButton").assertColor(OrbitalBlue)
            onNodeWithText(context.getString(R.string.numpad_button_submit)).assertIsDisplayed()
            onNodeWithTag("submitButton").performClick()

            verify {
                events.onClick()
            }
        }
    }

    @Test
    fun testDisabledSubmitButton() {

        val events: ButtonEvents = mockk(relaxed = true)

        with(composeTestRule) {
            setContent {
                SubmitButton(
                    isEnable = false,
                    onClick = events::onClick
                )
            }

            onNodeWithTag("submitButton").assertIsNotEnabled()
            onNodeWithTag("submitButton").assertColor(MagneticGrey)
            onNodeWithText(context.getString(R.string.numpad_button_submit)).assertIsDisplayed()
            onNodeWithTag("submitButton").performClick()

            verify(inverse = true) {
                events.onClick()
            }
        }
    }
}