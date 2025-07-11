@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.PinInputRequest
import de.tillhub.inputengine.contract.PinInputResult
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.PinInputViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

class PinInputScreenTest {

    private interface TestPinInputScreen {
        fun onResult(result: PinInputResult)
    }

    @Test
    fun testOnResult() = runComposeUiTest {
        val test = mock<TestPinInputScreen> {
            every { onResult(any()) } returns Unit
        }

        val request = PinInputRequest(
            pin = "1234",
            toolbarTitle = StringParam.Enable("Test Pin Input"),
            overridePinInput = true,
        )

        val viewModel = PinInputViewModel(
            request = request
        )

        setContent {
            AppTheme {
                PinInputScreen(
                    onResult = test::onResult,
                    viewModel = viewModel,
                )
            }
        }

        onNodeWithContentDescription("Toolbar title").assertTextEquals("Test Pin Input")
        onNodeWithTag("Pin placeholder").assertExists()

        onNodeWithContentDescription("Override pin").performClick()

        verify {
            test.onResult(PinInputResult.Success(emptyMap()))
        }
    }

    @Test
    fun testOnDismiss() = runComposeUiTest {
        val test = mock<TestPinInputScreen> {
            every { onResult(any()) } returns Unit
        }

        val request = PinInputRequest(
            pin = "1234",
            toolbarTitle = StringParam.Enable("Test Pin Input"),
            overridePinInput = true,
        )

        val viewModel = PinInputViewModel(
            request = request
        )

        setContent {
            AppTheme {
                PinInputScreen(
                    onResult = test::onResult,
                    viewModel = viewModel,
                )
            }
        }

        onNodeWithContentDescription("Toolbar back button").performClick()

        verify {
            test.onResult(PinInputResult.Canceled)
        }
    }
}