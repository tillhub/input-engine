package de.tillhub.inputengine.ui.moneyinput

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.AmountParam
import de.tillhub.inputengine.data.Amount
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Currency

class MoneyInputViewModel : ViewModel() {

    private var isInitValue = false
    private var isZeroAllowed = true
    private var amountMax: Amount = Amount.max()
    private var amountMin: Amount = Amount.min()
    private lateinit var currency: Currency

    private val _inputCurrencyAmountInput = MutableStateFlow(Amount.zero())

    val moneyInput: StateFlow<MoneyInputData> = _inputCurrencyAmountInput.map {
        MoneyInputData(
            price = it,
            text = MoneyFormatter.format(it.value, currency),
            isValid = isValid(it)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EMPTY
    )

    fun init(request: MoneyInputRequest) {
        this.isInitValue = true
        this.currency = request.currency
        this.isZeroAllowed = request.isZeroAllowed
        this.amountMax = when (request.amountMax) {
            AmountParam.Disable -> Amount.max()
            is AmountParam.Enable -> Amount.from(request.amountMax.amount)
        }
        this.amountMin = when (request.amountMin) {
            AmountParam.Disable -> Amount.min()
            is AmountParam.Enable -> Amount.from(request.amountMin.amount)
        }
        _inputCurrencyAmountInput.value = Amount.from(request.amount)
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val baseValue = if (isInitValue) Amount.zero() else _inputCurrencyAmountInput.value
                val newValue = Amount.append(baseValue, key.digit)
                isInitValue = false
                _inputCurrencyAmountInput.value = minOf(newValue, amountMax)
            }
        }
    }

    private fun clear() {
        _inputCurrencyAmountInput.value = Amount.zero()
    }

    private fun delete() {
        _inputCurrencyAmountInput.value = Amount.removeLastDigit(_inputCurrencyAmountInput.value)
    }

    private fun isValid(amount: Amount): Boolean {
        return when {
            isZeroAllowed -> isValueBetweenMinMax(amount)
            else -> amount.isNotZero() && isValueBetweenMinMax(amount)
        }
    }

    private fun isValueBetweenMinMax(amount: Amount): Boolean {
        return amount in amountMin..amountMax
    }
}

data class MoneyInputData(
    val price: Amount,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        val EMPTY = MoneyInputData(
            price = Amount.zero(),
            text = "",
            isValid = false
        )
    }
}

@Parcelize
sealed class InputResultStatus : Parcelable {
    data class Success(
        val amount: BigDecimal,
        val extras: Bundle
    ) : InputResultStatus()

    data object Cancel : InputResultStatus()
}