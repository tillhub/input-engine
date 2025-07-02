package de.tillhub.inputengine.ui.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.data.CurrencyIO
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.domain.StringParam
import de.tillhub.inputengine.formatting.MoneyFormatter
import de.tillhub.inputengine.domain.NumpadKey
import de.tillhub.inputengine.ui.amount.MoneyInputData.Companion.DEFAULT_CURRENCY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class AmountInputViewModel(
    private val request: AmountInputRequest,
    private val formatter: MoneyFormatter,
) : ViewModel() {

    val toolbarTitle: String get() = request.toolbarTitle
    val responseExtras: Map<String, String> get() = request.extras

    private val _uiMinValue: MutableStateFlow<StringParam> = MutableStateFlow(StringParam.Disable)
    val uiMinValue: StateFlow<StringParam> = _uiMinValue

    private val _uiMaxValue: MutableStateFlow<StringParam> = MutableStateFlow(StringParam.Disable)
    val uiMaxValue: StateFlow<StringParam> = _uiMaxValue

    private var negateNextDigit = false
    private var isInitValue = true
    internal var amountInputMode: AmountInputMode = AmountInputMode.BOTH
        private set

    /**
     * The max amount that can be entered
     * If the max amount is not set, the max amount of the MoneyIO is used
     */
    private var moneyMax: MoneyIO = when (request.amountMax) {
        MoneyParam.Disable -> MoneyIO.max(request.amount.currency)
        is MoneyParam.Enable -> {
            _uiMaxValue.value = StringParam.Enable(
                formatter.format(request.amountMax.amount)
            )
            request.amountMax.amount
        }
    }

    /**
     * The min amount that can be entered
     * If the min amount is not set, the min amount of the MoneyIO is used
     */
    private var moneyMin: MoneyIO = when (request.amountMin) {
        MoneyParam.Disable -> MoneyIO.min(request.amount.currency)
        is MoneyParam.Enable -> {
            _uiMinValue.value = StringParam.Enable(
                formatter.format(request.amountMin.amount)
            )
            request.amountMin.amount
        }
    }

    private val _inputCurrencyMoneyInput = MutableStateFlow(MoneyIO.zero(DEFAULT_CURRENCY))
    internal val moneyInput: StateFlow<MoneyInputData> = _inputCurrencyMoneyInput.map {
        val formattedAmount = formatAmount(it)
        MoneyInputData(
            money = when (amountInputMode) {
                AmountInputMode.POSITIVE -> if (it.isNegative()) -it else it
                AmountInputMode.NEGATIVE -> if (it.isPositive()) it.negate() else it
                AmountInputMode.BOTH -> it
            },
            text = formattedAmount.first,
            isValid = isValid(it),
            isHint = formattedAmount.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MoneyInputData.EMPTY,
    )

    init {
        setupAmountConstraints(request)
    }

    private fun setupAmountConstraints(request: AmountInputRequest) {
        when {
            moneyMin >= moneyMax -> {
                moneyMin = MoneyIO.min(request.amount.currency)
                moneyMax = MoneyIO.max(request.amount.currency)
                _uiMinValue.value = StringParam.Disable
                _uiMaxValue.value = StringParam.Disable
                _inputCurrencyMoneyInput.value = request.amount
            }

            moneyMin.isZero() && moneyMax.isPositive() -> {
                amountInputMode = AmountInputMode.POSITIVE
                _uiMinValue.value = StringParam.Disable
                _inputCurrencyMoneyInput.value = request.amount.abs()
            }

            moneyMin.isPositive() && moneyMax.isPositive() -> {
                amountInputMode = AmountInputMode.POSITIVE
                _inputCurrencyMoneyInput.value = request.amount.abs()
            }

            moneyMax.isZero() && moneyMin.isNegative() -> {
                amountInputMode = AmountInputMode.NEGATIVE
                moneyMax = -moneyMin
                moneyMin = MoneyIO.zero(moneyMin.currency)
                _uiMinValue.value = StringParam.Disable
                _uiMaxValue.value = StringParam.Enable(formatter.format(moneyMax))
                _inputCurrencyMoneyInput.value = request.amount.abs()
            }

            moneyMin.isNegative() && moneyMax.isNegative() -> {
                amountInputMode = AmountInputMode.NEGATIVE
                val temp = moneyMax
                moneyMax = -moneyMin
                moneyMin = temp.abs()
                _uiMinValue.value = StringParam.Enable(formatter.format(moneyMin))
                _uiMaxValue.value = StringParam.Enable(formatter.format(moneyMax))
                _inputCurrencyMoneyInput.value = request.amount.abs()
            }

            else -> {
                _inputCurrencyMoneyInput.value = request.amount
            }
        }
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val baseValue = if (isInitValue) {
                    MoneyIO.zero(request.amount.currency)
                } else {
                    _inputCurrencyMoneyInput.value
                }
                val newValue = if (negateNextDigit) {
                    MoneyIO.append(baseValue, key.digit.value).negate().also {
                        negateNextDigit = false
                    }
                } else {
                    MoneyIO.append(baseValue, key.digit.value)
                }
                isInitValue = false
                _inputCurrencyMoneyInput.value = if (moneyMin.isPositive()) {
                    minOf(newValue, moneyMax)
                } else {
                    maxOf(minOf(newValue, moneyMax), moneyMin)
                }
            }

            is NumpadKey.DecimalSeparator -> Unit
            NumpadKey.Negate -> negate()
        }
    }

    private fun negate() {
        if (_inputCurrencyMoneyInput.value.isZero()) {
            negateNextDigit = true
        }
        _inputCurrencyMoneyInput.value = _inputCurrencyMoneyInput.value.negate()
    }

    private fun clear() {
        _inputCurrencyMoneyInput.value = MoneyIO.zero(request.amount.currency)
    }

    private fun delete() {
        _inputCurrencyMoneyInput.value = MoneyIO.removeLastDigit(_inputCurrencyMoneyInput.value)
    }

    private fun isValid(money: MoneyIO): Boolean {
        return if (request.isZeroAllowed) isValueBetweenMinMax(money)
        else money.isNotZero() && isValueBetweenMinMax(money)
    }

    /**
     * Returns a pair of [formattedAmount] and [isHint]
     * @see MoneyInputData
     */
    private fun formatAmount(amount: MoneyIO): Pair<String, Boolean> {
        return if (request.hintAmount is MoneyParam.Enable && amount.isZero()) {
            formatter.format(request.hintAmount.amount) to true
        } else {
            formatter.format(amount) to false
        }
    }

    private fun isValueBetweenMinMax(money: MoneyIO): Boolean {
        return money in moneyMin..moneyMax
    }

    companion object {
        // Define a custom keys for our dependency
        val REQUEST_KEY = object : CreationExtras.Key<AmountInputRequest> {}
        val FORMATTER_KEY = object : CreationExtras.Key<MoneyFormatter> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val request = this[REQUEST_KEY] as AmountInputRequest
                val formatter = this[FORMATTER_KEY] as MoneyFormatter
                AmountInputViewModel(request,formatter)
            }
        }
    }
}
internal enum class AmountInputMode {
    POSITIVE,
    NEGATIVE,
    BOTH,
}

internal data class MoneyInputData(
    val money: MoneyIO,
    val text: String,
    val isValid: Boolean,
    val isHint: Boolean,
) {
    companion object {
        val DEFAULT_CURRENCY = CurrencyIO.forCode("EUR")
        val EMPTY = MoneyInputData(
            money = MoneyIO.zero(DEFAULT_CURRENCY),
            text = "",
            isValid = false,
            isHint = true,
        )
    }
}
