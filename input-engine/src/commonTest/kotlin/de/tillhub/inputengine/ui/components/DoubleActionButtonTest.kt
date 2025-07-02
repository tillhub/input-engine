@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.sp
import de.tillhub.inputengine.theme.AppTheme
import de.tillhub.inputengine.theme.GalacticBlue
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test

class DoubleActionButtonTest {

    @Test
    fun displaysAllElements() = runComposeUiTest {

        val repository = mock<TestHelper> {
            every { onClick() } returns Unit
            every { onLongClick() } returns Unit
        }

        setContent {
            AppTheme {
                DoubleActionButton(
                    modifier = Modifier.testTag("doubleActionButton"),
                    onClick = repository::onClick,
                    onLongClick = repository::onLongClick,
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

        verify {
            repository.onClick()
        }
    }

    private interface TestHelper {
        fun onClick()
        fun onLongClick()
    }
}