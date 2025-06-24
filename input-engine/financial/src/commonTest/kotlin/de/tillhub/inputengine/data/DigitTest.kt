package de.tillhub.inputengine.data

import de.tillhub.inputengine.financial.data.Digit
import kotlin.test.Test
import kotlin.test.assertEquals

class DigitTest {

    @Test
    fun testFrom() {
        assertEquals(Digit.ZERO, Digit.from(0))
        assertEquals(Digit.ONE, Digit.from(1))
        assertEquals(Digit.TWO, Digit.from(2))
        assertEquals(Digit.THREE, Digit.from(3))
        assertEquals(Digit.FOUR, Digit.from(4))
        assertEquals(Digit.FIVE, Digit.from(5))
        assertEquals(Digit.SIX, Digit.from(6))
        assertEquals(Digit.SEVEN, Digit.from(7))
        assertEquals(Digit.EIGHT, Digit.from(8))
        assertEquals(Digit.NINE, Digit.from(9))
    }
}
