package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.toBigInteger
import de.tillhub.inputengine.domain.Digit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test suite for NumberInputController functionality.
 *
 * NumberInputController manages numeric input by handling individual digits,
 * decimal places, sign changes, and various input operations. It's used in
 * input activities to build numbers from user interactions.
 *
 * The controller maintains separate lists for integer and fractional parts,
 * and handles operations like adding digits, deleting digits, switching signs,
 * and switching between integer and decimal input modes.
 */
class NumberInputControllerTest {
    private lateinit var target: NumberInputController

    @BeforeTest
    fun setUp() {
        target = NumberInputControllerImpl()
    }

    /**
     * Tests setValue() method with digit lists and sign parameter.
     *
     * This method allows setting the controller's value by providing:
     * - List of digits for the integer part
     * - List of digits for the fractional part
     * - Boolean flag for negative sign
     *
     * Verifies that the controller correctly combines these components
     * into the final numeric value.
     */
    @Test
    fun setValue() {
        // Test negative integer: digits [1,2] with negative sign = -12
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), true)
        assertEquals(
            (-12).toBigInteger(),
            target.value().toBigInteger(),
            "Should create negative integer -12 from digits [1,2] with negative sign",
        )

        // Test positive decimal: digits [5,7] for integer, [9] for fraction = 57.9
        target.clear()
        target.setValue(listOf(Digit.FIVE, Digit.SEVEN), listOf(Digit.NINE), false)
        assertEquals(
            57.9.toBigDecimal(),
            target.value().toBigDecimal(),
            "Should create positive decimal 57.9 from integer digits [5,7] and fractional digit [9]",
        )
    }

    /**
     * Tests setValue() method with numeric values (Int/Double).
     *
     * This overloaded method accepts direct numeric values and should
     * properly convert them to the controller's internal representation.
     *
     * Tests various scenarios:
     * - Integer values
     * - Decimal values with different precision
     * - Precision truncation (controller limits to 4 decimal places)
     */
    @Test
    fun setValue_number() {
        // Test simple integer values
        target.setValue(856)
        assertEquals(856.toBigInteger(), target.value().toBigInteger(), "Should set integer value 856")

        target.setValue(9876)
        assertEquals(9876.toBigInteger(), target.value().toBigInteger(), "Should set integer value 9876")

        // Test decimal values with various precision levels
        target.setValue(567.126)
        assertEquals(
            567.126.toBigDecimal(),
            target.value().toBigDecimal(),
            "Should set decimal value 567.126 with 3 decimal places",
        )

        target.setValue(567.1267)
        assertEquals(567.1267.toBigDecimal(), target.value().toBigDecimal(), "Should set decimal value 567.1267 with 4 decimal places")

        // Test precision truncation - input has 5 decimal places but controller limits to 4
        target.setValue(567.12671)
        assertEquals(
            567.1267.toBigDecimal(), // Note: truncated from 567.12671
            target.value().toBigDecimal(),
            "Should truncate 567.12671 to 567.1267 (4 decimal places max)",
        )

        target.setValue(367.12)
        assertEquals(367.12.toBigDecimal(), target.value().toBigDecimal(), "Should set decimal value 367.12 with 2 decimal places")
    }

    /**
     * Tests addDigit() method for appending digits to existing values.
     *
     * This simulates user input where digits are added one by one.
     * The digit should be appended to the current value in the appropriate
     * position (integer or fractional part depending on current mode).
     */
    @Test
    fun addDigit_appendsCorrectly() {
        // Start with value 12 (from digits [1,2])
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), false)

        // Add digit 9 - should append to integer part: 12 -> 129
        target.addDigit(Digit.NINE)
        assertEquals(129.toBigInteger(), target.value().toBigInteger(), "Should append digit 9 to value 12, resulting in 129")
    }

    /**
     * Tests deleteLast() method for removing the most recently entered digit.
     *
     * This simulates backspace functionality where users can undo their
     * last digit entry. The method should remove digits in reverse order
     * of entry, handling both fractional and integer parts correctly.
     *
     * Tests the complete deletion sequence from a decimal number back to zero.
     */
    @Test
    fun deleteLast_worksCorrectly() {
        // Start with decimal value 129.87
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE), // Integer part: 129
            listOf(Digit.EIGHT, Digit.SEVEN), // Fractional part: .87
            false, // Positive number
        )
        assertEquals(129.87.toBigDecimal(), target.value().toBigDecimal(), "Should start with decimal value 129.87")

        // Delete last fractional digit: 129.87 -> 129.8
        target.deleteLast()
        assertEquals(129.8.toBigDecimal(), target.value().toBigDecimal(), "Should delete last fractional digit: 129.87 -> 129.8")

        // Delete remaining fractional digit: 129.8 -> 129 (becomes integer)
        target.deleteLast()
        assertEquals(129.toBigInteger(), target.value().toBigInteger(), "Should delete remaining fractional digit: 129.8 -> 129")

        // Delete last integer digit: 129 -> 12
        target.deleteLast()
        assertEquals(12.toBigInteger(), target.value().toBigInteger(), "Should delete last integer digit: 129 -> 12")

        // Delete second integer digit: 12 -> 1
        target.deleteLast()
        assertEquals(1.toBigInteger(), target.value().toBigInteger(), "Should delete second integer digit: 12 -> 1")

        // Delete last remaining digit: 1 -> 0 (controller never goes below zero)
        target.deleteLast()
        assertEquals(0.toBigInteger(), target.value().toBigInteger(), "Should delete last digit and reset to 0: 1 -> 0")
    }

    /**
     * Tests switchToMinor() method for enabling decimal input mode.
     *
     * This method switches the controller from integer input mode to
     * fractional input mode, allowing subsequent digits to be added
     * to the decimal portion of the number.
     *
     * Simulates user pressing a decimal point button in the UI.
     */
    @Test
    fun switchToMinor_addsFraction() {
        // Start with integer value 56
        target.setValue(listOf(Digit.FIVE, Digit.SIX), emptyList(), false)
        assertEquals(56.toBigInteger(), target.value().toBigInteger(), "Should start with integer value 56")

        // Switch to decimal input mode and add digit 2
        target.switchToMinor(true)
        target.addDigit(Digit.TWO)

        // Should now be 56.2 (digit 2 added to fractional part)
        assertEquals(
            56.2.toBigDecimal(),
            target.value().toBigDecimal(),
            "Should add digit 2 to fractional part after switching to minor: 56 -> 56.2",
        )
    }

    /**
     * Tests switchNegate() method for toggling the sign of the current value.
     *
     * This simulates the +/- button functionality where users can change
     * a positive number to negative or vice versa without affecting the
     * absolute value of the number.
     */
    @Test
    fun switchNegate_flipsSign() {
        // Start with positive value 5
        target.setValue(5)

        // Toggle sign: 5 -> -5
        target.switchNegate()
        assertEquals((-5).toBigInteger(), target.value().toBigInteger(), "Should toggle sign from positive to negative: 5 -> -5")
    }

    /**
     * Tests clear() method for resetting the controller to initial state.
     *
     * This simulates the clear/reset button functionality where all
     * entered digits are removed and the value returns to zero.
     * Essential for allowing users to start over with their input.
     */
    @Test
    fun clear_resetsToZero() {
        // Start with a complex decimal value 129.87
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE), // Integer: 129
            listOf(Digit.EIGHT, Digit.SEVEN), // Fractional: .87
            false, // Positive
        )

        // Clear should reset everything to zero
        target.clear()
        assertEquals(0.toBigInteger(), target.value().toBigInteger(), "Should reset to zero after clear operation")
    }
}
