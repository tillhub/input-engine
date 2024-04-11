package de.tillhub.inputengine.ui.quantity

import androidx.lifecycle.ViewModel
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.Quantity
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuantityInputViewModel : ViewModel() {

    private val _mutableDisplayDataFlow = MutableStateFlow(QuantityInputData.EMPTY)
    val displayDataFlow: StateFlow<QuantityInputData> = _mutableDisplayDataFlow

    private val majorDigits: MutableList<Digit> = mutableListOf()
    private val minorDigits: MutableList<Digit> = mutableListOf()

    private var isDecimalSeparatorEntered = false
    private var nextKeyResetsCurrentValue = false

    private var minQuantity: Quantity = Quantity.MIN_VALUE
    private var maxQuantity: Quantity = Quantity.MAX_VALUE

    private var isZeroAllowed: Boolean = false
        set(value) {
            field = value
            updateDisplayData(displayDataFlow.value.qty)
        }

    private var allowsNegatives: Boolean = true
        set(value) {
            field = value
            updateDisplayData(displayDataFlow.value.qty)
        }

    init {
        setValue(Quantity.ZERO)
    }

    private fun reset() {
        majorDigits.clear()
        majorDigits.add(Digit.ZERO)
        minorDigits.clear()
        isDecimalSeparatorEntered = false
    }

    fun setValue(currentValue: Quantity) {
        majorDigits.clear()
        majorDigits.addAll(currentValue.majorDigits)

        minorDigits.clear()
        minorDigits.addAll(currentValue.minorDigits)

        isDecimalSeparatorEntered = minorDigits.isNotEmpty()

        updateDisplayData(currentValue)

        nextKeyResetsCurrentValue = true
    }

    fun setInitialValue(
        initialValue: Quantity,
        currentValue: Quantity,
        minValue: QuantityParam,
        maxValue: QuantityParam
    ) {
        this.minQuantity = when (minValue) {
            QuantityParam.Disable -> Quantity.MIN_VALUE
            is QuantityParam.Enable -> Quantity.of(minValue.value)
        }
        this.maxQuantity = when (maxValue) {
            QuantityParam.Disable -> Quantity.MAX_VALUE
            is QuantityParam.Enable -> Quantity.of(maxValue.value)
        }
        setValue(currentValue)
        _mutableDisplayDataFlow.value = QuantityInputData(
            qty = initialValue,
            text = QuantityFormatter.formatPlain(initialValue, minorDigits.size),
            isValid = isValid(initialValue)
        )
    }

    fun decrease() {
        val newValue = displayDataFlow.value.qty.nextSmaller(
            allowsZero = isZeroAllowed,
            allowsNegatives = allowsNegatives
        )
        if (isValid(newValue)) {
            setValue(newValue)
            nextKeyResetsCurrentValue = false
        }
    }

    fun increase() {
        val newValue = displayDataFlow.value.qty.nextLarger(
            maxQuantity = maxQuantity,
        )
        if (isValid(newValue)) {
            setValue(newValue)
            nextKeyResetsCurrentValue = false
        }
    }

    private fun clearValueIfNeeded() {
        if (nextKeyResetsCurrentValue) {
            nextKeyResetsCurrentValue = false
            reset()
        }
    }

    fun processKey(key: NumpadKey) {
        clearValueIfNeeded()

        val minorD = minorDigits.toMutableList()
        val majorD = majorDigits.toMutableList()

        when (key) {
            is NumpadKey.SingleDigit -> {
                if (isDecimalSeparatorEntered) {
                    if (minorD.size < MAX_ALLOWED_DIGITS) {
                        minorD.add(key.digit)
                    }
                } else {
                    if (majorD.size == 1 && majorD[0].value == 0) {
                        majorD.clear()
                        majorD.add(key.digit)
                    } else {
                        majorD.add(key.digit)
                    }
                }

                val quantity = Quantity.of(majorD, minorD)
                if (!quantity.isValid()) return
            }

            NumpadKey.Clear -> reset()

            NumpadKey.Delete -> {
                if (isDecimalSeparatorEntered) {
                    if (minorD.isEmpty()) {
                        isDecimalSeparatorEntered = false
                    } else {
                        minorD.removeLastOrNull()
                    }
                } else {
                    majorD.removeLastOrNull()
                }
            }

            NumpadKey.DecimalSeparator -> {
                if (!isDecimalSeparatorEntered) {
                    isDecimalSeparatorEntered = true
                }
            }
        }
        minorDigits.clear()
        minorDigits.addAll(minorD)
        majorDigits.clear()
        majorDigits.addAll(majorD)

        val tempQuantity = Quantity.of(majorDigits, minorDigits)

        val quantity = when {
            tempQuantity > maxQuantity -> {
                reset()
                maxQuantity
            }

            tempQuantity < minQuantity -> {
                reset()
                minQuantity
            }

            else -> tempQuantity
        }
        updateDisplayData(quantity)
    }

    private fun updateDisplayData(value: Quantity) {
        var text = QuantityFormatter.formatPlain(value, minorDigits.size)
        if (isDecimalSeparatorEntered && minorDigits.isEmpty()) {
            text += QuantityFormatter.getDecimalSeparator()
        }

        _mutableDisplayDataFlow.value = QuantityInputData(
            qty = value,
            text = text,
            isValid = isValid(value)
        )
    }

    private fun isValid(quantity: Quantity): Boolean {
        return if (isZeroAllowed) {
            isValueBetweenMinMax(quantity)
        } else {
            !quantity.isZero() && isValueBetweenMinMax(quantity)
        }
    }

    private fun isValueBetweenMinMax(quantity: Quantity): Boolean {
        return quantity in minQuantity..maxQuantity
    }

    companion object {
        private const val MAX_DECIMAL_DIGIT_COUNT: Int = 4
        private val MAX_ALLOWED_DIGITS: Int = minOf(MAX_DECIMAL_DIGIT_COUNT, Quantity.FRACTIONS)
    }
}

data class QuantityInputData(
    val qty: Quantity,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        val EMPTY = QuantityInputData(
            qty = Quantity.ZERO,
            text = QuantityFormatter.format(Quantity.ZERO),
            isValid = false
        )
    }
}
