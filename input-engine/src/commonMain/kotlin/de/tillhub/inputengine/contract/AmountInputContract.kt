package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.domain.serializer.MoneyIOSerializer
import de.tillhub.inputengine.data.MoneyParam
import de.tillhub.inputengine.resources.Res
import de.tillhub.inputengine.resources.numpad_title_amount
import kotlinx.serialization.Serializable

interface AmountInputContract {
    fun launchAmountInput(request: AmountInputRequest)
}

@Composable
expect fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit,
): AmountInputContract

@Serializable
data class AmountInputRequest(
    @Serializable(with = MoneyIOSerializer::class) val amount: MoneyIO,
    val isZeroAllowed: Boolean = false,
    val toolbarTitle: String = Res.string.numpad_title_amount.key,
    val amountMin: MoneyParam = MoneyParam.Disable,
    val amountMax: MoneyParam = MoneyParam.Disable,
    val hintAmount: MoneyParam = MoneyParam.Disable,
    val extras: Map<String, String> = emptyMap(),
)

@Serializable
sealed class AmountInputResult {
    @Serializable
    data class Success(
        @Serializable(with = MoneyIOSerializer::class) val amount: MoneyIO,
        val extras: Map<String, String> = emptyMap(),
    ) : AmountInputResult()

    @Serializable
    data object Canceled : AmountInputResult()
}
