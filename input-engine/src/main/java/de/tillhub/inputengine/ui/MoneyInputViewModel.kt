package de.tillhub.inputengine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.Money
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.extensions.format
import de.tillhub.inputengine.extensions.orTrue
import de.tillhub.inputengine.ui.MoneyInputData.Companion.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale

class MoneyInputViewModel : ViewModel() {

    private var isInitValue = false
    private var isZeroAllowed = true
    private var maxValue: Money? = null
    private var minValue: Money? = null
    private var currency: String? = null

    private val locale = Locale.getDefault(Locale.Category.FORMAT)
    private val _moneyInput = MutableStateFlow(Money.zero())

    val moneyInput: StateFlow<MoneyInputData> = _moneyInput.map {
        MoneyInputData(
            price = it,
            text = "$currency${it.value.format(2, locale)}",
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
        request.amountMax?.let { this.maxValue = Money(it.toDouble()) }
        request.amountMin?.let { this.minValue = Money(it.toDouble()) }
        _moneyInput.value = Money(request.amount.toDouble())
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val newValue = if (isInitValue) {
                    Money.append(Money.zero(), key.digit).also {
                        isInitValue = false
                    }
                } else {
                    Money.append(_moneyInput.value, key.digit)
                }

                _moneyInput.value = maxValue?.let { max ->
                    if (newValue > max) {
                        max
                    } else {
                        newValue
                    }
                } ?: newValue
            }
        }
    }

    private fun clear() {
        _moneyInput.value = Money.zero()
    }

    private fun delete() {
        _moneyInput.value = Money.removeLastDigit(_moneyInput.value)
    }

    private fun isValid(money: Money): Boolean {
        return when {
            isZeroAllowed -> isValueBetweenMinMax(money)
            else -> money.isNotZero() && isValueBetweenMinMax(money)
        }
    }

    private fun isValueBetweenMinMax(money: Money) =
        minValue?.let { money >= it }.orTrue() &&
                maxValue?.let { money <= it }.orTrue()
}

data class MoneyInputData(
    val price: Money,
    val text: String,
    val isValid: Boolean
) {
    companion object {
        val EMPTY = MoneyInputData(
            price = Money.zero(),
            text = "",
            isValid = false
        )
    }
}
