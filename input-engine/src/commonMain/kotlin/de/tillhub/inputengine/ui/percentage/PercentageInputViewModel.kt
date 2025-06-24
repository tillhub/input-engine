package de.tillhub.inputengine.ui.percentage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.PercentageInputRequest
import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.financial.param.PercentageParam
import de.tillhub.inputengine.formatter.PercentageFormatter
import de.tillhub.inputengine.formatter.defaultLocale
import de.tillhub.inputengine.helper.NumberInputController
import de.tillhub.inputengine.helper.NumberInputControllerContract
import de.tillhub.inputengine.helper.NumpadKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PercentageInputViewModel(
    private val inputController: NumberInputControllerContract = NumberInputController(maxMajorDigits = 3),
    private val locale: String = defaultLocale()
) : ViewModel() {

    private val _inputPercentIO = MutableStateFlow(PercentageInputData.EMPTY)
    val percentageInput: StateFlow<PercentageInputData> = _inputPercentIO

    private var isInitValue = false
    private var maxPercent: PercentIO = PercentIO.WHOLE
    private var minPercent: PercentIO = PercentIO.ZERO
    private var isZeroAllowed: Boolean = false
        set(value) {
            field = value
            setValue(percentageInput.value.percent)
        }

    fun init(request: PercentageInputRequest) {
        this.isInitValue = true
        this.isZeroAllowed = request.allowsZero
        minPercent = when (request.percentageMin) {
            PercentageParam.Disable -> PercentIO.ZERO
            is PercentageParam.Enable -> request.percentageMin.percent
        }
        maxPercent = when (request.percentageMax) {
            PercentageParam.Disable -> PercentIO.WHOLE
            is PercentageParam.Enable -> request.percentageMax.percent
        }

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
        } else tempPercentage

        setValue(percent)
    }

    private fun setValue(percent: PercentIO) {
        _inputPercentIO.value = PercentageInputData(
            percent = percent,
            text = PercentageFormatter.format(percent, inputController.minorDigits.size, locale),
            isValid = isValid(percent)
        )
    }

    private fun isValid(percentIO: PercentIO): Boolean {
        return if (isZeroAllowed) {
            isValueBetweenMinMax(percentIO)
        } else {
            percentIO.isNotZero() && isValueBetweenMinMax(percentIO)
        }
    }

    private fun isValueBetweenMinMax(percent: PercentIO): Boolean {
        return percent in minPercent..maxPercent
    }
}

data class PercentageInputData(
    val percent: PercentIO,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        val EMPTY = PercentageInputData(PercentIO.ZERO, "", false)
    }
}

internal fun providePercentageInputViewModelFactory(request: PercentageInputRequest) = viewModelFactory {
    initializer {
        PercentageInputViewModel().apply {
            init(request)
        }
    }
}