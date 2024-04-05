package de.tillhub.inputengine.ui.quantity

import androidx.lifecycle.ViewModel
import de.tillhub.inputengine.data.Digit
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.Quantity
import de.tillhub.inputengine.data.quantity.NumpadDisplayData
import de.tillhub.inputengine.formatter.QuantityFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuantityInputViewModel : ViewModel() {

    private val _mutableDisplayDataFlow = MutableStateFlow(
        NumpadDisplayData.create(
            data = Quantity.ZERO,
            text = QuantityFormatter.format(Quantity.ZERO),
            isValid = true
        )
    )

    val displayDataFlow: StateFlow<NumpadDisplayData<Quantity>> = _mutableDisplayDataFlow

    private val majorDigits: MutableList<Digit> = mutableListOf()
    private val minorDigits: MutableList<Digit> = mutableListOf()

    private var isDecimalSeparatorEntered = false
    private var nextKeyResetsCurrentValue = false

    private var maxQuantity: Quantity = Quantity.MAX_VALUE

    private var isZeroAllowed: Boolean = true
        set(value) {
            field = value
            updateDisplayData(displayDataFlow.value.currentValue.data)
        }

    private var allowsNegatives: Boolean = true
        set(value) {
            field = value
            updateDisplayData(displayDataFlow.value.currentValue.data)
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

    fun setInitialValue(initialValue: Quantity, currentValue: Quantity) {
        setValue(currentValue)
        _mutableDisplayDataFlow.value = displayDataFlow.value.setInitialValue(
            data = initialValue,
            text = QuantityFormatter.formatPlain(initialValue, minorDigits.size),
            isValid = isValid(initialValue)
        )
    }

    fun setInitialValue(
        initialValue: Quantity,
        currentValue: Quantity,
        minValue: Quantity?,
        maxValue: Quantity?
    ) {
        setInitialValue(initialValue, currentValue)
        this.maxQuantity = maxValue ?: Quantity.MAX_VALUE
        setValue(currentValue)
        _mutableDisplayDataFlow.value = displayDataFlow.value.setCurrentValueWithMinAndMaxValue(
            data = initialValue,
            text = QuantityFormatter.formatPlain(initialValue, minorDigits.size),
            isValid = isValid(initialValue),
            minValue = minValue?.let {
                QuantityFormatter.formatPlain(it, minorDigits.size)
            },
            maxValue = maxValue?.let {
                QuantityFormatter.formatPlain(it, minorDigits.size)
            }
        )
    }

    fun decrease() {
        setValue(
            displayDataFlow.value.currentValue.data.nextSmaller(
                allowsZero = isZeroAllowed,
                allowsNegatives = allowsNegatives
            )
        )
        nextKeyResetsCurrentValue = false
    }

    fun increase() {
        val newValue = displayDataFlow.value.currentValue.data.nextLarger(
            maxQuantity = maxQuantity,
        )
        if (!newValue.isValid()) return
        setValue(newValue)
        nextKeyResetsCurrentValue = false
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
        val quantity = if (tempQuantity <= maxQuantity) {
            tempQuantity
        } else {
            reset()
            maxQuantity
        }
        updateDisplayData(quantity)
    }

    fun updateDisplayData(value: Quantity) {
        var text = QuantityFormatter.formatPlain(value, minorDigits.size)
        if (isDecimalSeparatorEntered && minorDigits.isEmpty()) {
            text += QuantityFormatter.getDecimalSeparator()
        }

        _mutableDisplayDataFlow.value = displayDataFlow.value.setCurrentValue(
            data = value,
            text = text,
            isValid = isValid(value)
        )
    }

    private fun isValid(quantity: Quantity): Boolean {
        return if (isZeroAllowed) {
            true
        } else {
            !quantity.isZero()
        }
    }

    companion object {
        private const val MAX_DECIMAL_DIGIT_COUNT: Int = 4
        private val MAX_ALLOWED_DIGITS: Int = minOf(MAX_DECIMAL_DIGIT_COUNT, Quantity.FRACTIONS)
    }
}