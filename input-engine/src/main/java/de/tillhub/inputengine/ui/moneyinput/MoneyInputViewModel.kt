package de.tillhub.inputengine.ui.moneyinput

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.tillhub.inputengine.contract.MoneyInputRequest
import de.tillhub.inputengine.data.AmountParam
import de.tillhub.inputengine.data.Money
import de.tillhub.inputengine.data.NumpadKey
import de.tillhub.inputengine.ui.moneyinput.MoneyInputData.Companion.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Locale

class MoneyInputViewModel : ViewModel() {

    private var isInitValue = false
    private lateinit var request: MoneyInputRequest

    private val locale = Locale.getDefault(Locale.Category.FORMAT)
    private val _moneyInput = MutableStateFlow(Money.zero())

    val moneyInput: StateFlow<MoneyInputData> = _moneyInput.map {
        MoneyInputData(
            price = it,
            text = "${request.currency}${"%,.2f".format(locale, it.value)}",
            isValid = isValid(it)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EMPTY
    )

    fun init(request: MoneyInputRequest) {
        this.isInitValue = true
        this.request = request
        _moneyInput.value = Money(request.amount)
    }

    fun input(key: NumpadKey) {
        when (key) {
            NumpadKey.Clear -> clear()
            NumpadKey.Delete -> delete()
            is NumpadKey.SingleDigit -> {
                val baseValue = if (isInitValue) Money.zero() else _moneyInput.value
                val newValue = Money.append(baseValue, key.digit)
                isInitValue = false

                _moneyInput.value = if (request.amountMax is AmountParam.Enable) {
                    val amountMax =
                        Money((request.amountMax as AmountParam.Enable).amount)
                    minOf(newValue, amountMax)
                } else {
                    newValue
                }
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
            request.isZeroAllowed -> isValueBetweenMinMax(money)
            else -> money.isNotZero() && isValueBetweenMinMax(money)
        }
    }

    private fun isValueBetweenMinMax(money: Money): Boolean {
        val amountMin = request.amountMin as? AmountParam.Enable
        val amountMax = request.amountMax as? AmountParam.Enable
        return (amountMin == null || money.value >= amountMin.amount) &&
                (amountMax == null || money.value <= amountMax.amount)
    }
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

@Parcelize
sealed class InputResultStatus : Parcelable {
    data class Success(
        val amount: BigDecimal,
        val extras: Bundle
    ) : InputResultStatus()

    data object Cancel : InputResultStatus()
}