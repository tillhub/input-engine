@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.GalacticBlue
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleActionButtonTest {

    @Test
    fun performsClick() = runComposeUiTest {

        var onClickCount = 0
        var onLongClickCount = 0

        setContent {
            AppTheme {
                DoubleActionButton(
                    modifier = Modifier.testTag("doubleActionButton"),
                    onClick = { onClickCount++ },
                    onLongClick = { onLongClickCount++ },
                ) {
                    Text(
                        text = "leftActionText",
                        fontSize = 14.sp,
                        color = GalacticBlue,
                    )
                }
            }
        }

        onNodeWithTag("doubleActionButton").performClick()

        assertEquals(1, onClickCount, "onClick should be called once")
        assertEquals(0, onLongClickCount, "onLongClickCount should not be called on click")
    }

    @Test
    fun performsLongClick() = runComposeUiTest {

        var onClickCount = 0
        var onLongClickCount = 0

        setContent {
            AppTheme {
                DoubleActionButton(
                    modifier = Modifier.testTag("doubleActionButton"),
                    onClick = { onClickCount++ },
                    onLongClick = { onLongClickCount++ },
                ) {
                    Text(
                        text = "leftActionText",
                        fontSize = 14.sp,
                        color = GalacticBlue,
                    )
                }
            }
        }

        onNodeWithTag("doubleActionButton").performTouchInput {
            longClick()
        }

        assertEquals(0, onClickCount, "onClick should not be called on long click")
        assertEquals(1, onLongClickCount, "onLongClick should be called once")
    }

    @Test
    fun doesNotTriggerClicksWhenDisabled() = runComposeUiTest {
        var onClickCount = 0
        var onLongClickCount = 0

        setContent {
            AppTheme {
                DoubleActionButton(
                    modifier = Modifier.testTag("doubleActionButton"),
                    enabled = false,
                    onClick = { onClickCount++ },
                    onLongClick = { onLongClickCount++ },
                ) {
                    Text("Button Text")
                }
            }
        }

        onNodeWithTag("doubleActionButton").assertIsNotEnabled()

        // Try to click the disabled button
        onNodeWithTag("doubleActionButton").performClick()
        
        // Try to long click the disabled button
        onNodeWithTag("doubleActionButton").performTouchInput {
            longClick()
        }

        assertEquals(0, onClickCount, "onClick should not be called when disabled")
        assertEquals(0, onLongClickCount, "onLongClick should not be called when disabled")
    }
}