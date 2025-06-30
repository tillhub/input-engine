package de.tillhub.inputengine.ui.quantity

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.helper.getMajorDigits
import de.tillhub.inputengine.financial.helper.getMinorDigits
import de.tillhub.inputengine.financial.param.QuantityParam
import de.tillhub.inputengine.formatter.QuantityFormatter
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.helper.NumberInputControllerImpl
import de.tillhub.inputengine.helper.NumpadKey
import de.tillhub.inputengine.helper.defaultLocale
import de.tillhub.inputengine.ui.theme.MagneticGrey
import de.tillhub.inputengine.ui.theme.OrbitalBlue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class QuantityInputViewModel(
    private val inputController: NumberInputController = NumberInputControllerImpl(maxMajorDigits = 5),
    private val locale: String = defaultLocale(),
) : ViewModel() {

    private val _mutableDisplayDataFlow = MutableStateFlow(QuantityInputData.EMPTY)
    val displayDataFlow: StateFlow<QuantityInputData> = _mutableDisplayDataFlow

    private var nextKeyResetsCurrentValue = false
    private var quantityHint: QuantityParam = QuantityParam.Disable
    private var minQuantity: QuantityIO = QuantityIO.MIN_VALUE
    private var maxQuantity: QuantityIO = QuantityIO.MAX_VALUE

    private var isZeroAllowed: Boolean = false
        set(value) {
            field = value
            updateDisplayData(displayDataFlow.value.qty)
        }

    fun setInitialValue(request: QuantityInputRequest) {
        isZeroAllowed = request.allowsZero
        quantityHint = request.quantityHint
        minQuantity = when (request.minQuantity) {
            QuantityParam.Disable -> QuantityIO.MIN_VALUE
            is QuantityParam.Enable -> request.minQuantity.value
        }
        maxQuantity = when (request.maxQuantity) {
            QuantityParam.Disable -> QuantityIO.MAX_VALUE
            is QuantityParam.Enable -> request.maxQuantity.value
        }
        if (minQuantity >= maxQuantity) {
            minQuantity = QuantityIO.MIN_VALUE
            maxQuantity = QuantityIO.MAX_VALUE
        }
        setValue(request.quantity)
    }

    fun decrease() {
        val newValue = displayDataFlow.value.qty.nextSmaller(
            allowsZero = isZeroAllowed,
            allowsNegatives = minQuantity.isNegative(),
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

    fun processKey(key: NumpadKey) {
        clearValueIfNeeded()

        when (key) {
            is NumpadKey.SingleDigit -> inputController.addDigit(key.digit)
            NumpadKey.Clear -> inputController.clear()
            NumpadKey.Delete -> inputController.deleteLast()
            NumpadKey.DecimalSeparator -> inputController.switchToMinor(true)
            NumpadKey.Negate -> inputController.switchNegate()
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

    private fun setValue(currentValue: QuantityIO) {
        inputController.setValue(
            majorDigits = currentValue.getMajorDigits(),
            minorDigits = currentValue.getMinorDigits(),
            isNegative = currentValue.isNegative(),
        )
        updateDisplayData(currentValue)

        nextKeyResetsCurrentValue = true
    }

    private fun clearValueIfNeeded() {
        if (nextKeyResetsCurrentValue) {
            nextKeyResetsCurrentValue = false
            inputController.clear()
        }
    }

    private fun updateDisplayData(quantity: QuantityIO) {
        val (quantityText, quantityColor) = if (quantityHint is QuantityParam.Enable &&
            quantity.isZero() && !isZeroAllowed
        ) {
            QuantityFormatter.format(
                quantity = (quantityHint as QuantityParam.Enable).value,
                locale = locale,
            ) to MagneticGrey
        } else {
            QuantityFormatter.format(
                quantity = quantity,
                minFractionDigits = inputController.minorDigits.size,
                locale = locale,
            ) to OrbitalBlue
        }

        _mutableDisplayDataFlow.value = QuantityInputData(
            qty = quantity,
            text = quantityText,
            color = quantityColor,
            isValid = isValid(quantity),
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

internal data class QuantityInputData(
    val qty: QuantityIO,
    val text: String,
    val color: Color,
    val isValid: Boolean,
) {
    companion object {
        val EMPTY = QuantityInputData(
            qty = QuantityIO.ZERO,
            text = QuantityFormatter.format(QuantityIO.ZERO),
            color = MagneticGrey,
            isValid = false,
        )
    }
}

internal fun provideQuantityInputViewModelFactory(request: QuantityInputRequest) = viewModelFactory {
    initializer {
        QuantityInputViewModel().apply {
            setInitialValue(request)
        }
    }
}
