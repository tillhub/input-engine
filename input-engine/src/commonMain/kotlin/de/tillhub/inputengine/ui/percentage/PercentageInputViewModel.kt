package de.tillhub.inputengine.ui.percentage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.data.PercentIO
import de.tillhub.inputengine.data.PercentageParam
import de.tillhub.inputengine.data.mapToStringParam
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.domain.helper.NumberInputController
import de.tillhub.inputengine.domain.helper.NumberInputControllerImpl
import de.tillhub.inputengine.formatting.PercentageFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PercentageInputViewModel(
    private val request: PercentageInputRequest,
    private val formatter: PercentageFormatter,
    private val inputController: NumberInputController = NumberInputControllerImpl(maxMajorDigits = 3),
) : ViewModel() {

    val toolbarTitle: String get() = request.toolbarTitle
    val responseExtras: Map<String, String> get() = request.extras
    val allowDecimal: Boolean get() = request.allowDecimal

    val maxStringParam: StringParam get() = request.percentageMax.mapToStringParam {
        formatter.format(it)
    }
    val minStringParam: StringParam get() = request.percentageMin.mapToStringParam {
        formatter.format(it)
    }

    private val _percentageInput = MutableStateFlow(PercentageInputData.EMPTY)
    val percentageInput: StateFlow<PercentageInputData> = _percentageInput

    private var isInitValue = true
    private var maxPercent: PercentIO = when (request.percentageMax) {
        PercentageParam.Disable -> PercentIO.WHOLE
        is PercentageParam.Enable -> request.percentageMax.percent
    }
    private var minPercent: PercentIO = when (request.percentageMin) {
        PercentageParam.Disable -> PercentIO.ZERO
        is PercentageParam.Enable -> request.percentageMin.percent
    }

    init {
        inputController.setValue(request.percent.toDouble())
        setValue(request.percent)
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> inputController.clear()
            NumpadKey.DecimalSeparator -> inputController.switchToMinor(true)
            NumpadKey.Delete -> inputController.deleteLast()
            is NumpadKey.SingleDigit -> {
                if (isInitValue) {
                    inputController.clear()
                    isInitValue = false
                }
                inputController.addDigit(key.digit)
            }
            NumpadKey.Negate -> Unit
        }

        val tempPercentage = PercentIO.of(inputController.value())
        val percent = if (tempPercentage > maxPercent) {
            inputController.clear()
            maxPercent
        } else {
            tempPercentage
        }

        setValue(percent)
    }

    private fun setValue(percent: PercentIO) {
        _percentageInput.value = PercentageInputData(
            percent = percent,
            text = formatter.format(percent),
            isValid = isValid(percent),
        )
    }

    private fun isValid(percentIO: PercentIO): Boolean = if (request.allowsZero) {
        isValueBetweenMinMax(percentIO)
    } else {
        percentIO.isNotZero() && isValueBetweenMinMax(percentIO)
    }

    private fun isValueBetweenMinMax(percent: PercentIO): Boolean = percent in minPercent..maxPercent

    companion object {
        // Define a custom keys for our dependency
        val REQUEST_KEY = object : CreationExtras.Key<PercentageInputRequest> {}
        val FORMATTER_KEY = object : CreationExtras.Key<PercentageFormatter> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val request = this[REQUEST_KEY] as PercentageInputRequest
                val formatter = this[FORMATTER_KEY] as PercentageFormatter
                PercentageInputViewModel(
                    request = request,
                    formatter = formatter,
                )
            }
        }
    }
}

data class PercentageInputData(
    val percent: PercentIO,
    val text: String,
    val isValid: Boolean,
) {
    companion object {
        val EMPTY = PercentageInputData(PercentIO.ZERO, "", false)
    }
}
