@file:OptIn(ExperimentalTestApi::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.theme.AppTheme
import kotlin.test.Test

class PercentageInputPreviewTest {

    @Test
    fun displaysInitialPercentageText() = runComposeUiTest {
        setContent {
            AppTheme {
                PercentageInputPreview(
                    percentText = "0%",
                    percentageMin = StringParam.Disable,
                    percentageMax = StringParam.Disable
                )
            }
        }

        onNodeWithContentDescription("Max allowed percentage").assertDoesNotExist()
        onNodeWithContentDescription("Current percentage").assertTextEquals("0%")
        onNodeWithContentDescription("Min allowed percentage").assertDoesNotExist()
    }

    @Test
    fun displaysAllElementsWithMinAndMaxEnabled() = runComposeUiTest {
        setContent {
            AppTheme {
                PercentageInputPreview(
                    percentText = "20%",
                    percentageMin = StringParam.Enable("10%"),
                    percentageMax = StringParam.Enable("100%")
                )
            }
        }

        onNodeWithContentDescription("Max allowed percentage").assertTextEquals("max. 100%")
        onNodeWithContentDescription("Current percentage").assertTextEquals("20%")
        onNodeWithContentDescription("Min allowed percentage").assertTextEquals("min. 10%")
    }
}