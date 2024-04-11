package de.tillhub.inputengine.ui.moneyinput

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.contract.AmountResultStatus
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.data.Money
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.DEFAULT_CURRENCY
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.EMPTY
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.util.Currency

class MoneyInputViewModel : ViewModel() {

    private var isInitValue = false
    private var isZeroAllowed = true
    private lateinit var moneyMax: Money
    private lateinit var moneyMin: Money
    private lateinit var currency: Currency

    private val _inputCurrencyMoneyInput = MutableStateFlow(Money.zero(DEFAULT_CURRENCY))

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

    fun init(request: AmountResultStatus) {
        this.isInitValue = true
        this.currency = request.currency
        this.moneyMax = Money.max(request.currency)
        this.moneyMin = Money.min(request.currency)
        this.isZeroAllowed = request.isZeroAllowed
        this.moneyMax = when (request.amountMax) {
            MoneyParam.Disable -> Money.max(request.currency)
            is MoneyParam.Enable -> Money.from(request.amountMax.amount, request.currency)
        }
        this.moneyMin = when (request.amountMin) {
            MoneyParam.Disable -> Money.min(request.currency)
            is MoneyParam.Enable -> Money.from(request.amountMin.amount, request.currency)
        }
        _inputCurrencyMoneyInput.value = Money.from(request.amount, request.currency)
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val baseValue = if (isInitValue) Money.zero(currency) else _inputCurrencyMoneyInput.value
                val newValue = Money.append(baseValue, key.digit)
                isInitValue = false
                _inputCurrencyMoneyInput.value = minOf(newValue, moneyMax)
            }
        }
    }

    private fun clear() {
        _inputCurrencyMoneyInput.value = Money.zero(currency)
    }

    private fun delete() {
        _inputCurrencyMoneyInput.value = Money.removeLastDigit(_inputCurrencyMoneyInput.value)
    }

    private fun isValid(money: Money): Boolean {
        return when {
            isZeroAllowed -> isValueBetweenMinMax(money)
            else -> money.isNotZero() && isValueBetweenMinMax(money)
        }
    }

    private fun isValueBetweenMinMax(money: Money): Boolean {
        return money in moneyMin..moneyMax
    }
}

data class MoneyInputData(
    val money: Money,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        internal val DEFAULT_CURRENCY = Currency.getInstance("EUR")
        val EMPTY = MoneyInputData(
            money = Money.zero(DEFAULT_CURRENCY),
            text = "",
            isValid = false
        )
    }
}

@Parcelize
sealed class AmountInputResultStatus : Parcelable {
    data class Success(
        val amount: BigDecimal,
        val extras: Bundle
    ) : AmountInputResultStatus()

    data object Canceled : AmountInputResultStatus()
}