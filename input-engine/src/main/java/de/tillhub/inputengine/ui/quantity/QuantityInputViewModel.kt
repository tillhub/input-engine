package de.tillhub.inputengine.ui.quantity

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuantityInputViewModel : ViewModel() {

    private val _mutableDisplayDataFlow = MutableStateFlow(QuantityInputData.EMPTY)
    val displayDataFlow: StateFlow<QuantityInputData> = _mutableDisplayDataFlow

    private val inputController = NumberInputController(maxMajorDigits = 5)

    private var nextKeyResetsCurrentValue = false

    private var quantityHint: QuantityParam = QuantityParam.Disable
    private var minQuantity: QuantityIO = QuantityIO.MIN_VALUE
    private var maxQuantity: QuantityIO = QuantityIO.MAX_VALUE

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
        setValue(QuantityIO.ZERO)
    }

    fun setValue(currentValue: QuantityIO) {
        inputController.setValue(currentValue.majorDigits, currentValue.minorDigits)
        updateDisplayData(currentValue)

        nextKeyResetsCurrentValue = true
    }

    fun setInitialValue(request: QuantityInputRequest) {
        this.quantityHint = request.quantityHint
        this.minQuantity = when (request.minQuantity) {
            QuantityParam.Disable -> QuantityIO.MIN_VALUE
            is QuantityParam.Enable -> request.minQuantity.value
        }
        this.maxQuantity = when (request.maxQuantity) {
            QuantityParam.Disable -> QuantityIO.MAX_VALUE
            is QuantityParam.Enable -> request.maxQuantity.value
        }
        setValue(request.quantity)
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
            inputController.clear()
        }
    }

    fun processKey(key: NumpadKey) {
        clearValueIfNeeded()

        when (key) {
            is NumpadKey.SingleDigit -> inputController.addDigit(key.digit)
            NumpadKey.Clear -> inputController.clear()
            NumpadKey.Delete -> inputController.deleteLast()
            NumpadKey.DecimalSeparator -> inputController.switchToMinor(true)
        }

        val tempQuantity = QuantityIO.of(inputController.value())

        val quantity = when {
            tempQuantity > maxQuantity -> {
                inputController.clear()
                maxQuantity
            }

            tempQuantity < minQuantity -> {
                inputController.clear()
                minQuantity
            }

            else -> tempQuantity
        }
        updateDisplayData(quantity)
    }

    private fun updateDisplayData(quantity: QuantityIO) {
        val (quantityText, quantityColor) = if (quantityHint is QuantityParam.Enable && quantity.isZero()) {
            QuantityFormatter.format((quantityHint as QuantityParam.Enable).value) to MagneticGrey
        } else {
            QuantityFormatter.format(quantity, inputController.minorDigits.size) to OrbitalBlue
        }

        _mutableDisplayDataFlow.value = QuantityInputData(
            qty = quantity,
            text = quantityText,
            color = quantityColor,
            isValid = isValid(quantity)
        )
    }

    private fun isValid(quantity: QuantityIO): Boolean {
        return if (isZeroAllowed) {
            isValueBetweenMinMax(quantity)
        } else {
            !quantity.isZero() && isValueBetweenMinMax(quantity)
        }
    }

    private fun isValueBetweenMinMax(quantity: QuantityIO): Boolean {
        return quantity in minQuantity..maxQuantity
    }
}

data class QuantityInputData(
    val qty: QuantityIO,
    val text: String,
    val color: Color,
    val isValid: Boolean
) {
    companion object {
        val EMPTY = QuantityInputData(
            qty = QuantityIO.ZERO,
            text = QuantityFormatter.format(QuantityIO.ZERO),
            color = MagneticGrey,
            isValid = false
        )
    }
}
