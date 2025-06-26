//@file:OptIn(ExperimentalTestApi::class)
//
//package de.tillhub.inputengine.ui.components
//
//import androidx.compose.ui.test.ExperimentalTestApi
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.runComposeUiTest
//import de.tillhub.inputengine.financial.data.Digit
//import de.tillhub.inputengine.helper.NumpadKey
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//class NumpadKeyboardTest {
//    internal interface NumpadEvents {
//        fun onClick(key: NumpadKey)
//    }
//    @Test
//    fun testDefaultStateAndEvents() = runComposeUiTest {
//        //val events: NumpadEvents = mockk(relaxed = true)
//
//        setContent {
//            NumberKeyboard(
//                onClick = events::onClick
//            )
//        }
//
//        onNodeWithText("1").assertIsDisplayed()
//        onNodeWithText("2").assertIsDisplayed()
//        onNodeWithText("3").assertIsDisplayed()
//        onNodeWithText("4").assertIsDisplayed()
//        onNodeWithText("5").assertIsDisplayed()
//        onNodeWithText("6").assertIsDisplayed()
//        onNodeWithText("7").assertIsDisplayed()
//        onNodeWithText("8").assertIsDisplayed()
//        onNodeWithText("9").assertIsDisplayed()
//        onNodeWithText("0").assertIsDisplayed()
//        onNodeWithText("C").assertIsDisplayed()
//        onNodeWithText("←").assertIsDisplayed()
//        onNodeWithText(",").assertDoesNotExist()
//        onNodeWithText("-").assertDoesNotExist()
//
//        // Simulate input
//        onNodeWithText("1").performClick()
//        onNodeWithText("2").performClick()
//        onNodeWithText("3").performClick()
//
//        onNodeWithText("4").performClick()
//        onNodeWithText("5").performClick()
//        onNodeWithText("6").performClick()
//        onNodeWithText("7").performClick()
//        onNodeWithText("8").performClick()
//        onNodeWithText("9").performClick()
//        onNodeWithText("C").performClick()
//        onNodeWithText("0").performClick()
//        onNodeWithText("←").performClick()
//
//        val expected : List<NumpadKey> = listOf(
//            NumpadKey.SingleDigit(Digit.FOUR),
//            NumpadKey.SingleDigit(Digit.FIVE),
//            NumpadKey.SingleDigit(Digit.SIX),
//            NumpadKey.SingleDigit(Digit.SEVEN),
//            NumpadKey.SingleDigit(Digit.EIGHT),
//            NumpadKey.SingleDigit(Digit.NINE),
//            NumpadKey.Clear,
//            NumpadKey.SingleDigit(Digit.ZERO),
//            NumpadKey.Delete,
//        )
//        assertEquals(expected, clickedKeys)
//    }
//
//    @Test
//    fun testShowMinusAction() = runComposeUiTest {
//        setContent {
//            NumberKeyboard(
//                showNegative = true,
//                onClick = ::recordClick
//            )
//        }
//
//        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "←").forEach {
//            onNodeWithText(it).assertIsDisplayed()
//        }
//        onNodeWithText(",").assertDoesNotExist()
//        onNodeWithText("C").assertDoesNotExist()
//
//        onNodeWithText("-").performClick()
//
//        val expected: List<NumpadKey> = listOf(NumpadKey.Negate)
//        assertEquals(expected, clickedKeys)
//    }
//
//    @Test
//    fun testShowDecimal() = runComposeUiTest {
//        setContent {
//            NumberKeyboard(
//                showDecimalSeparator = true,
//                onClick = ::recordClick
//            )
//        }
//
//        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", "←").forEach {
//            onNodeWithText(it).assertIsDisplayed()
//        }
//        onNodeWithText("-").assertDoesNotExist()
//        onNodeWithText("C").assertDoesNotExist()
//
//        onNodeWithText(",").performClick()
//
//        val expected: List<NumpadKey> = listOf(NumpadKey.DecimalSeparator)
//        assertEquals(expected, clickedKeys)
//    }
//}
