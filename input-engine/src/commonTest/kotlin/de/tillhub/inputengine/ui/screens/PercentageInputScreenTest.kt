@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.contract.PercentageInputResult
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.PercentageFormatter
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.ui.PercentageInputViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

class PercentageInputScreenTest {

    private interface TestPercentageInputScreen {
        fun onResult(result: PercentageInputResult.Success)
        fun onDismiss()
    }

    /**
     * Test formatter that returns a predictable format for testing.
     * Returns "{percent}%" format (e.g., "25%" for 25% input).
     */
    private class TestPercentageFormatter : PercentageFormatter {
        override fun format(percent: PercentIO): String = "${percent.toInt()}%"
    }

    @Test
    fun testOnResult() = runComposeUiTest {
        val test = mock<TestPercentageInputScreen> {
            every { onResult(any()) } returns Unit
        }

        setContent {
            AppTheme {
                PercentageInputScreen(
                    onResult = test::onResult,
                    onDismiss = test::onDismiss,
                    viewModel = PercentageInputViewModel(
                        request = PercentageInputRequest(
                            percent = PercentIO.of(25),
                            percentageMin = PercentageParam.Enable(PercentIO.of(1)),
                            percentageMax = PercentageParam.Enable(PercentIO.of(90)),
                            toolbarTitle = StringParam.Enable("Test Percentage Input"),
                        ),
                        formatter = TestPercentageFormatter(),
                    ),
                )
            }
        }

        onNodeWithContentDescription("Toolbar title").assertTextEquals("Test Percentage Input")

        onNodeWithContentDescription("Max allowed percentage").assertTextEquals("max. 90%")
        onNodeWithContentDescription("Current percentage").assertTextEquals("25%")
        onNodeWithContentDescription("Min allowed percentage").assertTextEquals("min. 1%")

        onNodeWithContentDescription("Submit button").performClick()

        verify {
            test.onResult(
                result = PercentageInputResult.Success(
                    percent = PercentIO.of(25),
                ),
            )
        }
    }

    @Test
    fun testOnDismiss() = runComposeUiTest {
        val test = mock<TestPercentageInputScreen> {
            every { onDismiss() } returns Unit
        }

        setContent {
            AppTheme {
                PercentageInputScreen(
                    onResult = test::onResult,
                    onDismiss = test::onDismiss,
                    viewModel = PercentageInputViewModel(
                        request = PercentageInputRequest(
                            percent = PercentIO.of(25),
                            percentageMin = PercentageParam.Enable(PercentIO.of(1)),
                            percentageMax = PercentageParam.Enable(PercentIO.of(90)),
                            toolbarTitle = StringParam.Enable("Test Percentage Input"),
                        ),
                        formatter = TestPercentageFormatter(),
                    ),
                )
            }
        }

        onNodeWithContentDescription("Toolbar back button").performClick()

        verify {
            test.onDismiss()
        }
    }
}
