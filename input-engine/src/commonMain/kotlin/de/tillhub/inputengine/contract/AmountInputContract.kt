package de.tillhub.inputengine.contract

import androidx.compose.runtime.Composable
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.serializer.MoneyIOSerializer
import de.tillhub.inputengine.financial.param.MoneyParam
import kotlinx.serialization.Serializable

interface AmountInputContract {
    fun launchAmountInput(request: AmountInputRequest,)
}

@Composable
expect fun rememberAmountInputLauncher(
    onResult: (AmountInputResult) -> Unit
): AmountInputContract

@Serializable
data class AmountInputRequest(
    val amount: MoneyIO,
    val isZeroAllowed: Boolean = false,
    val amountMin: MoneyParam = MoneyParam.Disable,
    val amountMax: MoneyParam = MoneyParam.Disable,
    val hintAmount: MoneyParam = MoneyParam.Disable,
    val extras: Map<String, String> = emptyMap()
)

@Serializable
sealed class AmountInputResult {
    @Serializable
    data class Success(
        @Serializable(with = MoneyIOSerializer::class) val amount: MoneyIO,
        val extras: Map<String, String> = emptyMap()
    ) : AmountInputResult()

    @Serializable
    data object Canceled : AmountInputResult()
}
