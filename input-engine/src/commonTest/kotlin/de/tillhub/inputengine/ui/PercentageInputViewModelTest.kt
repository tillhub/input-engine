package de.tillhub.inputengine.ui

import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.PercentageFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for PercentageInputViewModel.
 *
 * Tests cover:
 * - Initialization with different request configurations
 * - Numpad input handling (digits, decimal, clear, delete)
 * - Percentage validation (min/max constraints, zero handling)
 * - Formatting and display text
 * - State flow emissions and reactivity
 * - Edge cases and boundary conditions
 */
class PercentageInputViewModelTest {
    /**
     * Test formatter that returns a predictable format for testing.
     * Returns "{percent}%" format (e.g., "25.5%" for 25.5% input).
     */
    private class TestPercentageFormatter : PercentageFormatter {
        override fun format(percent: PercentIO): String = "${percent.toDouble()}%"
    }

    private val testFormatter = TestPercentageFormatter()

    /**
     * Test initialization with default request.
     * Verifies that ViewModel initializes with correct default values and properties.
     */
    @Test
    fun initializesWithDefaultRequest() = runTest {
        // Given: Default request
        val request = PercentageInputRequest()
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // Then: Verify initial state
        assertEquals(
            StringParam.Disable,
            viewModel.toolbarTitle,
            "Should use default toolbar title",
        )
        assertEquals(emptyMap(), viewModel.responseExtras, "Should have empty extras")
        assertFalse(viewModel.allowDecimal, "Should not allow decimal by default")
        assertEquals(
            StringParam.Disable,
            viewModel.maxStringParam,
            "Should have disabled max param",
        )
        assertEquals(
            StringParam.Disable,
            viewModel.minStringParam,
            "Should have disabled min param",
        )

        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.ZERO,
            percentageInput.percent,
            "Should start with zero percent",
        )
        assertEquals("0.0%", percentageInput.text, "Should display formatted zero percent")
        assertFalse(
            percentageInput.isValid,
            "Zero should be invalid by default (allowsZero = false)",
        )
    }

    /**
     * Test initialization with custom request parameters.
     * Verifies that ViewModel correctly processes all request parameters.
     */
    @Test
    fun initializesWithCustomRequest() = runTest {
        // Given: Custom request with all parameters
        val initialPercent = PercentIO.Companion.of(15.5)
        val minPercent = PercentIO.Companion.of(5.0)
        val maxPercent = PercentIO.Companion.of(50.0)
        val request =
            PercentageInputRequest(
                percent = initialPercent,
                allowsZero = true,
                toolbarTitle = StringParam.Enable("Custom Title"),
                allowDecimal = true,
                percentageMin = PercentageParam.Enable(minPercent),
                percentageMax = PercentageParam.Enable(maxPercent),
                extras = mapOf("key1" to "value1", "key2" to "value2"),
            )
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // Then: Verify all properties are set correctly
        assertEquals(
            StringParam.Enable("Custom Title"),
            viewModel.toolbarTitle,
            "Should use custom toolbar title",
        )
        assertEquals(
            mapOf("key1" to "value1", "key2" to "value2"),
            viewModel.responseExtras,
            "Should preserve extras",
        )
        assertTrue(viewModel.allowDecimal, "Should allow decimal when specified")
        assertEquals(
            StringParam.Enable("50.0%"),
            viewModel.maxStringParam,
            "Should have enabled max param with formatted value",
        )
        assertEquals(
            StringParam.Enable("5.0%"),
            viewModel.minStringParam,
            "Should have enabled min param with formatted value",
        )

        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            initialPercent,
            percentageInput.percent,
            "Should start with specified initial percent",
        )
        assertEquals("15.5%", percentageInput.text, "Should display formatted initial percent")
        assertTrue(
            percentageInput.isValid,
            "Initial percent should be valid (within min/max range)",
        )
    }

    /**
     * Test single digit input handling.
     * Verifies that digit inputs are processed correctly and update the state.
     */
    @Test
    fun handlesSingleDigitInput() = runTest {
        // Given: ViewModel with zero initial value
        val request = PercentageInputRequest(allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input digit 5
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify state is updated
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(5), percentageInput.percent, "Should update to 5%")
        assertEquals("5.0%", percentageInput.text, "Should display formatted 5%")
        assertTrue(percentageInput.isValid, "5% should be valid")
    }

    /**
     * Test multiple digit input handling.
     * Verifies that consecutive digit inputs build up the percentage value correctly.
     */
    @Test
    fun handlesMultipleDigitInput() = runTest {
        // Given: ViewModel with zero initial value
        val request = PercentageInputRequest(allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input digits 2, 5 to create 25%
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify state is updated to 25%
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(25), percentageInput.percent, "Should update to 25%")
        assertEquals("25.0%", percentageInput.text, "Should display formatted 25%")
        assertTrue(percentageInput.isValid, "25% should be valid")
    }

    /**
     * Test decimal input handling when allowed.
     * Verifies that decimal separator input works correctly when allowDecimal is true.
     */
    @Test
    fun handlesDecimalInputWhenAllowed() = runTest {
        // Given: ViewModel with decimal allowed
        val request = PercentageInputRequest(allowDecimal = true, allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input 1, decimal separator, 5 to create 1.5%
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.DecimalSeparator)
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify decimal percentage is created
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(1.5), percentageInput.percent, "Should update to 1.5%")
        assertEquals("1.5%", percentageInput.text, "Should display formatted 1.5%")
        assertTrue(percentageInput.isValid, "1.5% should be valid")
    }

    /**
     * Test clear input functionality.
     * Verifies that clear input resets the percentage to zero.
     */
    @Test
    fun handlesClearInput() = runTest {
        // Given: ViewModel with some initial value
        val request =
            PercentageInputRequest(percent = PercentIO.Companion.of(25), allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Clear input
        viewModel.input(NumpadKey.Clear)

        // Then: Verify state is reset to zero
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.ZERO, percentageInput.percent, "Should reset to 0%")
        assertEquals("0.0%", percentageInput.text, "Should display formatted 0%")
        assertTrue(percentageInput.isValid, "0% should be valid when allowsZero = true")
    }

    /**
     * Test delete input functionality.
     * Verifies that delete input removes the last entered digit.
     */
    @Test
    fun handlesDeleteInput() = runTest {
        // Given: ViewModel with multi-digit value
        val request = PercentageInputRequest(allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // Set up initial value of 25%
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // When: Delete last digit
        viewModel.input(NumpadKey.Delete)

        // Then: Verify last digit is removed (25% -> 2%)
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.of(2),
            percentageInput.percent,
            "Should update to 2% after deleting last digit",
        )
        assertEquals("2.0%", percentageInput.text, "Should display formatted 2%")
        assertTrue(percentageInput.isValid, "2% should be valid")
    }

    /**
     * Test negate input handling.
     * Verifies that negate input is ignored (percentages are always positive).
     */
    @Test
    fun ignoresNegateInput() = runTest {
        // Given: ViewModel with some value
        val initialPercent = PercentIO.Companion.of(15)
        val request = PercentageInputRequest(percent = initialPercent, allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Try to negate
        viewModel.input(NumpadKey.Negate)

        // Then: Verify value remains unchanged
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            initialPercent,
            percentageInput.percent,
            "Should remain unchanged after negate",
        )
        assertEquals("15.0%", percentageInput.text, "Should still display 15%")
        assertTrue(percentageInput.isValid, "15% should still be valid")
    }

    /**
     * Test maximum percentage constraint enforcement.
     * Verifies that input exceeding maximum percentage is capped at the maximum.
     */
    @Test
    fun enforcesMaximumPercentageConstraint() = runTest {
        // Given: ViewModel with maximum percentage of 50%
        val maxPercent = PercentIO.Companion.of(50)
        val request =
            PercentageInputRequest(
                percentageMax = PercentageParam.Enable(maxPercent),
                allowsZero = true,
            )
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Try to input 75% (exceeds maximum)
        viewModel.input(NumpadKey.SingleDigit(Digit.SEVEN))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify value is capped at maximum
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(maxPercent, percentageInput.percent, "Should be capped at maximum 50%")
        assertEquals("50.0%", percentageInput.text, "Should display formatted maximum 50%")
        assertTrue(percentageInput.isValid, "Maximum percentage should be valid")
    }

    /**
     * Test minimum percentage validation.
     * Verifies that percentages below minimum are marked as invalid.
     */
    @Test
    fun validatesMinimumPercentageConstraint() = runTest {
        // Given: ViewModel with minimum percentage of 10%
        val minPercent = PercentIO.Companion.of(10)
        val request =
            PercentageInputRequest(
                percentageMin = PercentageParam.Enable(minPercent),
                allowsZero = true,
            )
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input 5% (below minimum)
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify value is invalid
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(5), percentageInput.percent, "Should have 5% value")
        assertEquals("5.0%", percentageInput.text, "Should display formatted 5%")
        assertFalse(percentageInput.isValid, "5% should be invalid (below minimum 10%)")
    }

    /**
     * Test zero percentage validation when not allowed.
     * Verifies that zero percentage is invalid when allowsZero is false.
     */
    @Test
    fun validatesZeroPercentageWhenNotAllowed() = runTest {
        // Given: ViewModel with allowsZero = false
        val request = PercentageInputRequest(allowsZero = false)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Clear to get zero percentage
        viewModel.input(NumpadKey.Clear)

        // Then: Verify zero is invalid
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.ZERO, percentageInput.percent, "Should have 0% value")
        assertEquals("0.0%", percentageInput.text, "Should display formatted 0%")
        assertFalse(percentageInput.isValid, "0% should be invalid when allowsZero = false")
    }

    /**
     * Test zero percentage validation when allowed.
     * Verifies that zero percentage is valid when allowsZero is true.
     */
    @Test
    fun validatesZeroPercentageWhenAllowed() = runTest {
        // Given: ViewModel with allowsZero = true
        val request = PercentageInputRequest(allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Clear to get zero percentage
        viewModel.input(NumpadKey.Clear)

        // Then: Verify zero is valid
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.ZERO, percentageInput.percent, "Should have 0% value")
        assertEquals("0.0%", percentageInput.text, "Should display formatted 0%")
        assertTrue(percentageInput.isValid, "0% should be valid when allowsZero = true")
    }

    /**
     * Test percentage within valid range.
     * Verifies that percentages within min/max constraints are valid.
     */
    @Test
    fun validatesPercentageWithinRange() = runTest {
        // Given: ViewModel with min 10% and max 50%
        val minPercent = PercentIO.Companion.of(10)
        val maxPercent = PercentIO.Companion.of(50)
        val request =
            PercentageInputRequest(
                percentageMin = PercentageParam.Enable(minPercent),
                percentageMax = PercentageParam.Enable(maxPercent),
                allowsZero = false,
            )
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input 25% (within range)
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify percentage is valid
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(25), percentageInput.percent, "Should have 25% value")
        assertEquals("25.0%", percentageInput.text, "Should display formatted 25%")
        assertTrue(percentageInput.isValid, "25% should be valid (within 10%-50% range)")
    }

    /**
     * Test initial value replacement on first digit input.
     * Verifies that the initial value is cleared when the first digit is entered.
     */
    @Test
    fun replacesInitialValueOnFirstDigitInput() = runTest {
        // Given: ViewModel with initial value of 30%
        val initialPercent = PercentIO.Companion.of(30)
        val request = PercentageInputRequest(percent = initialPercent, allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // Verify initial state
        val initialInput = viewModel.percentageInput.first()
        assertEquals(initialPercent, initialInput.percent, "Should start with 30%")

        // When: Input first digit (should replace initial value)
        viewModel.input(NumpadKey.SingleDigit(Digit.SEVEN))

        // Then: Verify initial value is replaced, not appended
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.of(7),
            percentageInput.percent,
            "Should replace initial value with 7%",
        )
        assertEquals("7.0%", percentageInput.text, "Should display formatted 7%")
        assertTrue(percentageInput.isValid, "7% should be valid")
    }

    /**
     * Test subsequent digit inputs after initial replacement.
     * Verifies that after the initial value is replaced, subsequent digits are appended normally.
     */
    @Test
    fun appendsSubsequentDigitsAfterInitialReplacement() = runTest {
        // Given: ViewModel with initial value
        val request =
            PercentageInputRequest(percent = PercentIO.Companion.of(30), allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input multiple digits (first should replace, second should append)
        viewModel.input(NumpadKey.SingleDigit(Digit.SEVEN))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify digits are combined correctly (75%, not just 5%)
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.of(75),
            percentageInput.percent,
            "Should combine digits to 75%",
        )
        assertEquals("75.0%", percentageInput.text, "Should display formatted 75%")
        assertTrue(percentageInput.isValid, "75% should be valid")
    }

    /**
     * Test complex input sequence with decimal.
     * Verifies a realistic input sequence with decimal values.
     */
    @Test
    fun handlesComplexInputSequenceWithDecimal() = runTest {
        // Given: ViewModel allowing decimals
        val request = PercentageInputRequest(allowDecimal = true, allowsZero = true)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input complex sequence: 1, 2, decimal, 7, 5 to create 12.75%
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.DecimalSeparator)
        viewModel.input(NumpadKey.SingleDigit(Digit.SEVEN))
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify complex decimal percentage
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(PercentIO.Companion.of(12.75), percentageInput.percent, "Should create 12.75%")
        assertEquals("12.75%", percentageInput.text, "Should display formatted 12.75%")
        assertTrue(percentageInput.isValid, "12.75% should be valid")
    }

    /**
     * Test edge case: maximum percentage boundary.
     * Verifies behavior at the exact maximum percentage boundary.
     */
    @Test
    fun handlesMaximumPercentageBoundary() = runTest {
        // Given: ViewModel with maximum of exactly 100%
        val maxPercent = PercentIO.Companion.WHOLE // 100%
        val request =
            PercentageInputRequest(
                percentageMax = PercentageParam.Enable(maxPercent),
                allowsZero = true,
            )
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // When: Input exactly 100%
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.ZERO))
        viewModel.input(NumpadKey.SingleDigit(Digit.ZERO))

        // Then: Verify 100% is accepted and valid
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.WHOLE,
            percentageInput.percent,
            "Should accept exactly 100%",
        )
        assertEquals("100.0%", percentageInput.text, "Should display formatted 100%")
        assertTrue(percentageInput.isValid, "100% should be valid at boundary")
    }

    /**
     * Test formatter integration.
     * Verifies that the formatter is called correctly and its output is used for display text.
     */
    @Test
    fun integratesWithFormatterCorrectly() = runTest {
        // Given: Custom formatter that adds prefix
        val customFormatter =
            object : PercentageFormatter {
                override fun format(percent: PercentIO): String = "Value: ${percent.toDouble()}%"
            }
        val request =
            PercentageInputRequest(percent = PercentIO.Companion.of(42.5), allowsZero = true)
        val viewModel = PercentageInputViewModel(request, customFormatter)

        // Then: Verify custom formatter output is used
        val percentageInput = viewModel.percentageInput.first()
        assertEquals(
            PercentIO.Companion.of(42.5),
            percentageInput.percent,
            "Should have correct percentage value",
        )
        assertEquals("Value: 42.5%", percentageInput.text, "Should use custom formatter output")
        assertTrue(percentageInput.isValid, "42.5% should be valid")
    }

    /**
     * Test response extras preservation.
     * Verifies that response extras from the request are preserved and accessible.
     */
    @Test
    fun preservesResponseExtras() = runTest {
        // Given: Request with complex extras
        val extras =
            mapOf(
                "sessionId" to "abc123",
                "userId" to "user456",
                "context" to "percentage_input",
            )
        val request = PercentageInputRequest(extras = extras)
        val viewModel = PercentageInputViewModel(request, testFormatter)

        // Then: Verify extras are preserved
        assertEquals(extras, viewModel.responseExtras, "Should preserve all response extras")
        assertEquals("abc123", viewModel.responseExtras["sessionId"], "Should preserve sessionId")
        assertEquals("user456", viewModel.responseExtras["userId"], "Should preserve userId")
        assertEquals(
            "percentage_input",
            viewModel.responseExtras["context"],
            "Should preserve context",
        )
    }
}
