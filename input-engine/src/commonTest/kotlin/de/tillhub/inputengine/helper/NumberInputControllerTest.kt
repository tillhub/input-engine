package de.tillhub.inputengine.helper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.toBigInteger
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.helper.toBigDecimal
import de.tillhub.inputengine.financial.helper.toBigInteger
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberInputControllerTest {

    private lateinit var target: NumberInputController

    @BeforeTest
    fun setUp() {
        target = NumberInputController()
    }

    @Test
    fun setValue() {
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), true)
        assertEquals((-12).toBigInteger(), target.value().toBigInteger())

        target.clear()
        target.setValue(listOf(Digit.FIVE, Digit.SEVEN), listOf(Digit.NINE), false)
        assertEquals(57.9.toBigDecimal(), target.value().toBigDecimal())
    }

    @Test
    fun setValue_number() {
        target.setValue(856)
        assertEquals(856.toBigInteger(), target.value().toBigInteger())

        target.setValue(9876)
        assertEquals(9876.toBigInteger(), target.value().toBigInteger())

        target.setValue(567.126)
        assertEquals(
            567.126.toBigDecimal(),
            target.value().toBigDecimal()
        )

        target.setValue(567.1267)
        assertEquals(567.1267.toBigDecimal(), target.value().toBigDecimal())

        target.setValue(567.12671)
        assertEquals(
            567.1267.toBigDecimal(),
            target.value().toBigDecimal()
        )

        target.setValue(367.12)
        assertEquals(367.12.toBigDecimal(), target.value().toBigDecimal())
    }

    @Test
    fun addDigit_appendsCorrectly() {
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), false)
        target.addDigit(Digit.NINE)
        assertEquals(129.toBigInteger(), target.value().toBigInteger())
    }

    @Test
    fun deleteLast_worksCorrectly() {
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE),
            listOf(Digit.EIGHT, Digit.SEVEN),
            false
        )
        assertEquals(129.87.toBigDecimal(), target.value().toBigDecimal())

        target.deleteLast()
        assertEquals(129.8.toBigDecimal(), target.value().toBigDecimal())

        target.deleteLast()
        assertEquals(129.toBigInteger(), target.value().toBigInteger())

        target.deleteLast()
        assertEquals(12.toBigInteger(), target.value().toBigInteger())

        target.deleteLast()
        assertEquals(1.toBigInteger(), target.value().toBigInteger())

        target.deleteLast()
        assertEquals(0.toBigInteger(), target.value().toBigInteger())
    }

    @Test
    fun switchToMinor_addsFraction() {
        target.setValue(listOf(Digit.FIVE, Digit.SIX), emptyList(), false)
        assertEquals(56.toBigInteger(), target.value().toBigInteger())

        target.switchToMinor(true)
        target.addDigit(Digit.TWO)
        assertEquals(56.2.toBigDecimal(), target.value().toBigDecimal())
    }

    @Test
    fun switchNegate_flipsSign() {
        target.setValue(5)
        target.switchNegate()
        assertEquals((-5).toBigInteger(), target.value().toBigInteger())
    }

    @Test
    fun clear_resetsToZero() {
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE),
            listOf(Digit.EIGHT, Digit.SEVEN),
            false
        )
        target.clear()
        assertEquals(0.toBigInteger(), target.value().toBigInteger())
    }
}

