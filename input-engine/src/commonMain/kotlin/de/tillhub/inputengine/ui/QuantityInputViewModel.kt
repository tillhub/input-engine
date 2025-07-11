package de.tillhub.inputengine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.QuantityInputRequest
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.data.QuantityParam
import de.tillhub.inputengine.data.mapToStringParam
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.domain.helper.NumberInputController
import de.tillhub.inputengine.domain.helper.NumberInputControllerImpl
import de.tillhub.inputengine.domain.helper.getMajorDigits
import de.tillhub.inputengine.domain.helper.getMinorDigits
import de.tillhub.inputengine.formatting.QuantityFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class QuantityInputViewModel(
    private val request: QuantityInputRequest,
    private val formatter: QuantityFormatter,
    private val inputController: NumberInputController = NumberInputControllerImpl(maxMajorDigits = 5),
) : ViewModel() {

    val toolbarTitle: StringParam get() = request.toolbarTitle
    val responseExtras: Map<String, String> get() = request.extras
    val allowDecimal: Boolean get() = request.allowDecimal
    val allowNegative: Boolean get() = when (val minQty = request.minQuantity) {
        QuantityParam.Disable -> true
        is QuantityParam.Enable -> minQty.value.isNegative()
    }

    val maxStringParam: StringParam = request.maxQuantity.mapToStringParam {
        formatter.format(it)
    }
    val minStringParam: StringParam = request.minQuantity.mapToStringParam {
        formatter.format(it)
    }

    private val _quantityInputFlow = MutableStateFlow(QuantityInputData.EMPTY)
    val quantityInputFlow: StateFlow<QuantityInputData> = _quantityInputFlow

    private var nextKeyResetsCurrentValue = false

    private var minQuantity: QuantityIO = when (request.minQuantity) {
        QuantityParam.Disable -> QuantityIO.MIN_VALUE
        is QuantityParam.Enable -> request.minQuantity.value
    }
    private var maxQuantity: QuantityIO = when (request.maxQuantity) {
        QuantityParam.Disable -> QuantityIO.MAX_VALUE
        is QuantityParam.Enable -> request.maxQuantity.value
    }

    init {
        setValue(request.quantity)
    }

    fun decrease() {
        val newValue = quantityInputFlow.value.qty.nextSmaller(
            allowsZero = request.allowsZero,
            allowsNegatives = minQuantity.isNegative(),
        )
        if (isValid(newValue)) {
            setValue(newValue)
            nextKeyResetsCurrentValue = false
        }
    }

    fun increase() {
        val newValue = quantityInputFlow.value.qty.nextLarger(
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
        val formattedQuantity = formatQuantity(quantity)

        _quantityInputFlow.value = QuantityInputData(
            qty = quantity,
            text = formattedQuantity.first,
            isValid = isValid(quantity),
            isHint = formattedQuantity.second,
        )
    }

    private fun formatQuantity(qty: QuantityIO): Pair<String, Boolean> = if (request.hintQuantity is QuantityParam.Enable && qty.isZero()) {
        formatter.format(request.hintQuantity.value) to true
    } else {
        formatter.format(qty) to false
    }

    private fun isValid(quantity: QuantityIO): Boolean = if (request.allowsZero) {
        isValueBetweenMinMax(quantity)
    } else {
        !quantity.isZero() && isValueBetweenMinMax(quantity)
    }

    private fun isValueBetweenMinMax(quantity: QuantityIO): Boolean = quantity in minQuantity..maxQuantity

    companion object {
        // Define a custom keys for our dependency
        val REQUEST_KEY = object : CreationExtras.Key<QuantityInputRequest> {}
        val FORMATTER_KEY = object : CreationExtras.Key<QuantityFormatter> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val request = this[REQUEST_KEY] as QuantityInputRequest
                val formatter = this[FORMATTER_KEY] as QuantityFormatter
                QuantityInputViewModel(request, formatter)
            }
        }
    }
}

internal data class QuantityInputData(
    val qty: QuantityIO,
    val text: String,
    val isValid: Boolean,
    val isHint: Boolean,
) {
    companion object {
        val EMPTY = QuantityInputData(
            qty = QuantityIO.ZERO,
            text = "0.0",
            isValid = false,
            isHint = true,
        )
    }
}
