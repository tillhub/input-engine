
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.ui.components.SubmitButton
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SubmitButtonTest {

    @Test
    fun testEnabledSubmitButton() = runComposeUiTest {
        var clicked = false

        setContent {
            SubmitButton(
                isEnable = true,
                onClick = { clicked = true },
            )
        }

        onNodeWithContentDescription("submitButton").assertIsEnabled()
        onNodeWithText("Submit").assertIsDisplayed() // Replace with raw text if R.string not accessible
        onNodeWithContentDescription("submitButton").performClick()

        assertTrue(clicked)
    }

    @Test
    fun testDisabledSubmitButton() = runComposeUiTest {
        var clicked = false

        setContent {
            SubmitButton(
                isEnable = false,
                onClick = { clicked = true },
            )
        }

        onNodeWithContentDescription("submitButton").assertIsNotEnabled()
        onNodeWithText("Submit").assertIsDisplayed()
        onNodeWithContentDescription("submitButton").performClick()

        assertFalse(clicked)
    }
}
