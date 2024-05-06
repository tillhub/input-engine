package de.tillhub.inputengine.ui.moneyinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.contract.AmountInputRequest
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.DEFAULT_CURRENCY
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Currency

class MoneyInputViewModel : ViewModel() {

    private var isInitValue = false
    private var isZeroAllowed = true
    private var negateNextDigit = false
    private lateinit var initAmount: MoneyIO
    private lateinit var moneyMax: MoneyIO
    private lateinit var moneyMin: MoneyIO

    private val _inputCurrencyMoneyInput = MutableStateFlow(MoneyIO.zero(DEFAULT_CURRENCY))
    val moneyInput: StateFlow<MoneyInputData> = _inputCurrencyMoneyInput.map {
        MoneyInputData(
            money = it,
            text = MoneyFormatter.format(it),
            isValid = isValid(it)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EMPTY
    )

    fun init(request: AmountInputRequest) {
        isInitValue = true
        isZeroAllowed = request.isZeroAllowed
        initAmount = request.amount
        moneyMax = when (request.amountMax) {
            MoneyParam.Disable -> MoneyIO.max(request.amount.currency)
            is MoneyParam.Enable -> request.amountMax.amount
        }
        moneyMin = when (request.amountMin) {
            MoneyParam.Disable -> MoneyIO.min(request.amount.currency)
            is MoneyParam.Enable -> request.amountMin.amount
        }
        if (moneyMin >= moneyMax) {
            moneyMin = MoneyIO.min(request.amount.currency)
            moneyMax = MoneyIO.max(request.amount.currency)
        }
        if (moneyMax.isZero() && moneyMin.isNegative()) {
            negateNextDigit = true
        }
        _inputCurrencyMoneyInput.value = request.amount
    }

    internal fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val baseValue = if (isInitValue) {
                    MoneyIO.zero(initAmount.currency)
                } else {
                    _inputCurrencyMoneyInput.value
                }
                val newValue = if (negateNextDigit) {
                    MoneyIO.append(baseValue, key.digit).negate().also {
                        negateNextDigit = false
                    }
                } else {
                    MoneyIO.append(baseValue, key.digit)
                }
                isInitValue = false
                _inputCurrencyMoneyInput.value = maxOf(minOf(newValue, moneyMax), moneyMin)
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
        _inputCurrencyMoneyInput.value = MoneyIO.zero(initAmount.currency)
    }

    private fun delete() {
        _inputCurrencyMoneyInput.value = MoneyIO.removeLastDigit(_inputCurrencyMoneyInput.value)
    }

    private fun isValid(money: MoneyIO): Boolean {
        return when {
            isZeroAllowed -> isValueBetweenMinMax(money)
            else -> money.isNotZero() && isValueBetweenMinMax(money)
        }
    }

    private fun isValueBetweenMinMax(money: MoneyIO): Boolean {
        return money in moneyMin..moneyMax
    }
}

data class MoneyInputData(
    val money: MoneyIO,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        internal val DEFAULT_CURRENCY = Currency.getInstance("EUR")
        val EMPTY = MoneyInputData(
            money = MoneyIO.zero(DEFAULT_CURRENCY),
            text = "",
            isValid = false
        )
    }
}