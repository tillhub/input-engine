package de.tillhub.inputengine.ui.amount

import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.data.CurrencyIO
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.MoneyFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for AmountInputViewModel covering all key functionality:
 * - Initialization with various AmountInputRequest configurations
 * - Input handling for all NumpadKey types
 * - Validation logic for zero allowance and min/max constraints
 * - Formatting behavior including hint amount display
 * - State flow emissions and correctness
 * - Edge cases and constraint handling
 */
class AmountInputViewModelTest {
    /**
     * Simple stub implementation of MoneyFormatter for testing.
     * Returns a predictable format: "{currency}{amount}" (e.g., "EUR1000" for €10.00)
     */
    private class TestMoneyFormatter : MoneyFormatter {
        override fun format(money: MoneyIO): String = "${money.currency.isoCode}${money.toDouble()}"
    }

    private lateinit var testFormatter: MoneyFormatter
    private val eur = CurrencyIO.forCode("EUR")
    private val usd = CurrencyIO.forCode("USD")

    @BeforeTest
    fun setup() {
        testFormatter = TestMoneyFormatter()
    }

    /**
     * Test basic initialization with minimal AmountInputRequest configuration.
     * Verifies that ViewModel initializes with correct default values and state flows.
     */
    @Test
    fun initializesWithBasicRequest() = runTest {
        // Given: Basic request with just an amount
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(10_00, eur), // €10.00
                toolbarTitle = "Test Amount Input",
            )

        // When: Creating ViewModel
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify initial state
        assertEquals("Test Amount Input", viewModel.toolbarTitle, "Toolbar title should match request")
        assertEquals(emptyMap(), viewModel.responseExtras, "Response extras should be empty by default")
        assertEquals(AmountInputMode.BOTH, viewModel.amountInputMode, "Default input mode should be BOTH")

        // Verify initial state flows
        val initialMinValue = viewModel.uiMinValue.first()
        val initialMaxValue = viewModel.uiMaxValue.first()
        val initialMoneyInput = viewModel.moneyInput.first()

        assertEquals(StringParam.Disable, initialMinValue, "Min value should be disabled by default")
        assertEquals(StringParam.Disable, initialMaxValue, "Max value should be disabled by default")
        assertEquals(MoneyIO.of(10_00, eur), initialMoneyInput.money, "Initial money should match request amount")
        assertTrue(initialMoneyInput.isValid, "Initial amount should be valid")
        assertFalse(initialMoneyInput.isHint, "Initial amount should not be a hint")
    }

    /**
     * Test initialization with complex AmountInputRequest including min/max constraints,
     * hint amount, zero allowance, and extras.
     */
    @Test
    fun initializesWithComplexRequest() = runTest {
        // Given: Complex request with all parameters
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(25_00, eur), // €25.00
                isZeroAllowed = true,
                toolbarTitle = "Complex Amount Input",
                amountMin = MoneyParam.Enable(MoneyIO.of(5_00, eur)), // €5.00
                amountMax = MoneyParam.Enable(MoneyIO.of(100_00, eur)), // €100.00
                hintAmount = MoneyParam.Enable(MoneyIO.of(50_00, eur)), // €50.00
                extras = mapOf("source" to "test", "id" to "123"),
            )

        // When: Creating ViewModel
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify initialization
        assertEquals("Complex Amount Input", viewModel.toolbarTitle, "Toolbar title should match request")
        assertEquals(mapOf("source" to "test", "id" to "123"), viewModel.responseExtras, "Response extras should match request")

        // Verify state flows with constraints
        val minValue = viewModel.uiMinValue.first()
        val maxValue = viewModel.uiMaxValue.first()
        val moneyInput = viewModel.moneyInput.first()

        assertTrue(minValue is StringParam.Enable, "Min value should be enabled when constraint provided")
        assertTrue(maxValue is StringParam.Enable, "Max value should be enabled when constraint provided")
        assertEquals(MoneyIO.of(25_00, eur), moneyInput.money, "Initial money should match request amount")
        assertTrue(moneyInput.isValid, "Initial amount should be valid within constraints")
    }

    /**
     * Test initialization with positive-only range (min=0, max>0).
     * Should set input mode to POSITIVE and configure constraints accordingly.
     */
    @Test
    fun initializesWithPositiveOnlyRange() = runTest {
        // Given: Request with positive-only range
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(15_00, eur),
                amountMin = MoneyParam.Enable(MoneyIO.zero(eur)), // €0.00
                amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)), // €50.00
            )

        // When: Creating ViewModel
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify positive-only mode
        assertEquals(AmountInputMode.POSITIVE, viewModel.amountInputMode, "Input mode should be POSITIVE for zero-to-positive range")

        val minValue = viewModel.uiMinValue.first()
        val maxValue = viewModel.uiMaxValue.first()

        assertEquals(StringParam.Disable, minValue, "Min value should be disabled when zero")
        assertTrue(maxValue is StringParam.Enable, "Max value should be enabled")
    }

    /**
     * Test initialization with negative-only range (min<0, max<0).
     * Should set input mode to NEGATIVE and swap/transform constraints.
     */
    @Test
    fun initializesWithNegativeOnlyRange() = runTest {
        // Given: Request with negative-only range
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(-15_00, eur), // -€15.00
                amountMin = MoneyParam.Enable(MoneyIO.of(-50_00, eur)), // -€50.00
                amountMax = MoneyParam.Enable(MoneyIO.of(-5_00, eur)), // -€5.00
            )

        // When: Creating ViewModel
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify negative-only mode
        assertEquals(AmountInputMode.NEGATIVE, viewModel.amountInputMode, "Input mode should be NEGATIVE for negative-only range")

        val minValue = viewModel.uiMinValue.first()
        val maxValue = viewModel.uiMaxValue.first()

        assertTrue(minValue is StringParam.Enable, "Min value should be enabled in negative mode")
        assertTrue(maxValue is StringParam.Enable, "Max value should be enabled in negative mode")
    }

    /**
     * Test initialization with invalid constraints (min >= max).
     * Should reset to default min/max values and disable constraints.
     */
    @Test
    fun initializesWithInvalidConstraints() = runTest {
        // Given: Request with invalid constraints (min >= max)
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(25_00, eur),
                amountMin = MoneyParam.Enable(MoneyIO.of(50_00, eur)), // €50.00
                amountMax = MoneyParam.Enable(MoneyIO.of(30_00, eur)), // €30.00 (invalid: min > max)
            )

        // When: Creating ViewModel
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify constraints are disabled due to invalid range
        val minValue = viewModel.uiMinValue.first()
        val maxValue = viewModel.uiMaxValue.first()

        assertEquals(StringParam.Disable, minValue, "Min value should be disabled when constraints are invalid")
        assertEquals(StringParam.Disable, maxValue, "Max value should be disabled when constraints are invalid")
        assertEquals(AmountInputMode.BOTH, viewModel.amountInputMode, "Input mode should default to BOTH when constraints are invalid")
    }

    /**
     * Test single digit input handling.
     * Verifies that digit input correctly updates the money value and state flows.
     */
    @Test
    fun handlesSingleDigitInput() = runTest {
        // Given: ViewModel with zero initial amount
        val request = AmountInputRequest(amount = MoneyIO.zero(eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Inputting digit 5
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify money value is updated
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.of(5, eur), moneyInput.money, "Money should be updated to 5 cents after digit input")
        assertTrue(moneyInput.isValid, "Money input should be valid after digit input")
        assertFalse(moneyInput.isHint, "Money input should not be a hint after user input")
    }

    /**
     * Test multiple digit input sequence.
     * Verifies that sequential digit inputs correctly build up the amount.
     */
    @Test
    fun handlesMultipleDigitInputSequence() = runTest {
        // Given: ViewModel with zero initial amount
        val request = AmountInputRequest(amount = MoneyIO.zero(eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Inputting sequence 1, 2, 3, 4 (should result in €12.34)
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE))
        viewModel.input(NumpadKey.SingleDigit(Digit.TWO))
        viewModel.input(NumpadKey.SingleDigit(Digit.THREE))
        viewModel.input(NumpadKey.SingleDigit(Digit.FOUR))

        // Then: Verify final amount
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.of(1234, eur), moneyInput.money, "Money should be €12.34 after inputting 1,2,3,4")
        assertTrue(moneyInput.isValid, "Money input should be valid")
    }

    /**
     * Test clear functionality.
     * Verifies that clear resets the amount to zero.
     */
    @Test
    fun handlesClearInput() = runTest {
        // Given: ViewModel with some initial amount
        val request = AmountInputRequest(amount = MoneyIO.of(25_00, eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Clearing the input
        viewModel.input(NumpadKey.Clear)

        // Then: Verify amount is reset to zero
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.zero(eur), moneyInput.money, "Money should be zero after clear")
    }

    /**
     * Test delete functionality.
     * Verifies that delete removes the last digit from the amount.
     */
    @Test
    fun handlesDeleteInput() = runTest {
        // Given: ViewModel with initial amount €12.34
        val request = AmountInputRequest(amount = MoneyIO.of(1234, eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Deleting once
        viewModel.input(NumpadKey.Delete)

        // Then: Verify last digit is removed (€12.34 -> €1.23)
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.of(123, eur), moneyInput.money, "Money should be €1.23 after deleting last digit from €12.34")
    }

    /**
     * Test negate functionality in BOTH mode.
     * Verifies that negate sets the negateNextDigit flag for next input.
     */
    @Test
    fun handlesNegateInput() = runTest {
        // Given: ViewModel in BOTH mode with zero amount
        val request =
            AmountInputRequest(
                amount = MoneyIO.zero(eur),
                amountMin = MoneyParam.Enable(MoneyIO.of(-50_00, eur)),
                amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)),
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Pressing negate then inputting digit 5
        viewModel.input(NumpadKey.Negate)
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify amount is negative
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.of(-5, eur), moneyInput.money, "Money should be negative after negate + digit input")
    }

    /**
     * Test decimal separator input (should be ignored).
     * AmountInputViewModel doesn't handle decimal separators, so input should be ignored.
     */
    @Test
    fun handlesDecimalSeparatorInput() = runTest {
        // Given: ViewModel with some amount
        val request = AmountInputRequest(amount = MoneyIO.of(10_00, eur))
        val viewModel = AmountInputViewModel(request, testFormatter)
        val initialMoney = viewModel.moneyInput.first().money

        // When: Inputting decimal separator
        viewModel.input(NumpadKey.DecimalSeparator)

        // Then: Verify amount is unchanged (decimal separator is ignored)
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(initialMoney, moneyInput.money, "Money should be unchanged after decimal separator input (ignored)")
    }

    /**
     * Test amount constraint enforcement with maximum limit.
     * Verifies that input is capped at the maximum allowed amount.
     */
    @Test
    fun enforcesMaximumAmountConstraint() = runTest {
        // Given: ViewModel with max constraint of €50.00
        val request =
            AmountInputRequest(
                amount = MoneyIO.zero(eur),
                amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)),
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Trying to input amount larger than max (inputting 9999 -> €99.99)
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))

        // Then: Verify amount is capped at maximum
        val moneyInput = viewModel.moneyInput.first()
        assertTrue(moneyInput.money <= MoneyIO.of(50_00, eur), "Money should not exceed maximum constraint of €50.00")
    }

    /**
     * Test amount constraint enforcement with minimum limit in negative range.
     * Verifies that input respects minimum constraints in negative scenarios.
     */
    @Test
    fun enforcesMinimumAmountConstraint() = runTest {
        // Given: ViewModel with min constraint of -€30.00 and max of €50.00
        val request =
            AmountInputRequest(
                amount = MoneyIO.zero(eur),
                amountMin = MoneyParam.Enable(MoneyIO.of(-30_00, eur)),
                amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)),
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Trying to input very large negative amount
        viewModel.input(NumpadKey.Negate)
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))
        viewModel.input(NumpadKey.SingleDigit(Digit.NINE))

        // Then: Verify amount respects minimum constraint
        val moneyInput = viewModel.moneyInput.first()
        assertTrue(moneyInput.money >= MoneyIO.of(-30_00, eur), "Money should not go below minimum constraint of -€30.00")
    }

    /**
     * Test zero validation with zero allowed.
     * Verifies that zero amounts are considered valid when isZeroAllowed is true.
     */
    @Test
    fun validatesZeroWhenAllowed() = runTest {
        // Given: ViewModel with zero allowed
        val request =
            AmountInputRequest(
                amount = MoneyIO.zero(eur),
                isZeroAllowed = true,
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify zero amount is valid
        val moneyInput = viewModel.moneyInput.first()
        assertTrue(moneyInput.isValid, "Zero amount should be valid when isZeroAllowed is true")
        assertEquals(MoneyIO.zero(eur), moneyInput.money, "Money should be zero")
    }

    /**
     * Test zero validation with zero not allowed.
     * Verifies that zero amounts are considered invalid when isZeroAllowed is false.
     */
    @Test
    fun invalidatesZeroWhenNotAllowed() = runTest {
        // Given: ViewModel with zero not allowed (default)
        val request = AmountInputRequest(amount = MoneyIO.zero(eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify zero amount is invalid
        val moneyInput = viewModel.moneyInput.first()
        assertFalse(moneyInput.isValid, "Zero amount should be invalid when isZeroAllowed is false (default)")
    }

    /**
     * Test hint amount display when amount is zero.
     * Verifies that hint amount is shown and marked as hint when main amount is zero.
     */
    @Test
    fun displaysHintAmountWhenZero() = runTest {
        // Given: ViewModel with hint amount configured
        val hintAmount = MoneyIO.of(25, eur)
        val request =
            AmountInputRequest(
                amount = MoneyIO.zero(eur),
                hintAmount = MoneyParam.Enable(hintAmount),
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Then: Verify hint is displayed
        val moneyInput = viewModel.moneyInput.first()
        assertEquals("EUR25.0", moneyInput.text, "Should display formatted hint amount when main amount is zero")
        assertTrue(moneyInput.isHint, "Should be marked as hint")
        assertEquals(MoneyIO.zero(eur), moneyInput.money, "Underlying money should still be zero")
    }

    /**
     * Test currency consistency.
     * Verifies that all operations maintain the same currency as the initial request.
     */
    @Test
    fun maintainsCurrencyConsistency() = runTest {
        // Given: ViewModel with USD currency
        val request = AmountInputRequest(amount = MoneyIO.of(10_00, usd))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Performing various operations
        viewModel.input(NumpadKey.Clear) // Clear to €0.00
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE)) // €0.05

        // Then: Verify currency is maintained
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(usd, moneyInput.money.currency, "Currency should remain USD throughout operations")
    }

    /**
     * Test state flow reactivity.
     * Verifies that state flows emit new values when the underlying state changes.
     */
    @Test
    fun emitsStateFlowUpdates() = runTest {
        // Given: ViewModel with initial amount
        val request = AmountInputRequest(amount = MoneyIO.of(10_00, eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // Capture initial state
        val initialMoney = viewModel.moneyInput.first().money

        // When: Changing the amount
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE))

        // Then: Verify state flow emits new value
        val updatedMoney = viewModel.moneyInput.first().money
        assertTrue(updatedMoney != initialMoney, "State flow should emit new value after input change")
    }

    /**
     * Test edge case: multiple clear operations.
     * Verifies that multiple clears don't cause issues and maintain zero state.
     */
    @Test
    fun handlesMultipleClearOperations() = runTest {
        // Given: ViewModel with some amount
        val request = AmountInputRequest(amount = MoneyIO.of(25_00, eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Clearing multiple times
        viewModel.input(NumpadKey.Clear)
        viewModel.input(NumpadKey.Clear)
        viewModel.input(NumpadKey.Clear)

        // Then: Verify amount remains zero
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.zero(eur), moneyInput.money, "Money should remain zero after multiple clear operations")
    }

    /**
     * Test edge case: delete on zero amount.
     * Verifies that delete operations on zero amounts are handled gracefully.
     */
    @Test
    fun handlesDeleteOnZeroAmount() = runTest {
        // Given: ViewModel with zero amount
        val request = AmountInputRequest(amount = MoneyIO.zero(eur))
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Trying to delete from zero
        viewModel.input(NumpadKey.Delete)

        // Then: Verify amount remains zero (no crash or unexpected behavior)
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.zero(eur), moneyInput.money, "Money should remain zero when deleting from zero amount")
    }

    /**
     * Test complex input sequence with constraints.
     * Verifies a realistic user input scenario with various operations and constraints.
     */
    @Test
    fun handlesComplexInputSequenceWithConstraints() = runTest {
        // Given: ViewModel with constraints and initial amount
        val request =
            AmountInputRequest(
                amount = MoneyIO.of(5_00, eur),
                isZeroAllowed = false,
                amountMin = MoneyParam.Enable(MoneyIO.of(1_00, eur)), // €1.00
                amountMax = MoneyParam.Enable(MoneyIO.of(99_00, eur)), // €99.00
                extras = mapOf("test" to "complex"),
            )
        val viewModel = AmountInputViewModel(request, testFormatter)

        // When: Performing complex input sequence
        viewModel.input(NumpadKey.Clear) // Clear to €0.00
        viewModel.input(NumpadKey.SingleDigit(Digit.ONE)) // €0.01
        viewModel.input(NumpadKey.SingleDigit(Digit.FIVE)) // €0.15
        viewModel.input(NumpadKey.Delete) // €0.01
        viewModel.input(NumpadKey.SingleDigit(Digit.ZERO)) // €0.10
        viewModel.input(NumpadKey.SingleDigit(Digit.ZERO)) // €1.00

        // Then: Verify final state
        val moneyInput = viewModel.moneyInput.first()
        assertEquals(MoneyIO.of(1_00, eur), moneyInput.money, "Money should be €1.00 after complex input sequence")
        assertTrue(moneyInput.isValid, "Amount should be valid (meets minimum constraint)")
        assertEquals(mapOf("test" to "complex"), viewModel.responseExtras, "Response extras should be preserved")
    }
}
