package de.tillhub.inputengine.financial.helper

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.financial.data.CurrencyIO
import de.tillhub.inputengine.financial.data.MoneyIO

val Number.eur: MoneyIO
    get() = MoneyIO.of(
        this.toDouble().toBigDecimal().moveDecimalPoint(2).doubleValue(false), EUR
    )
val BigNumber<*>.eur: MoneyIO
    get() = when (this) {
        is BigDecimal -> MoneyIO.of(this.moveDecimalPoint(2).doubleValue(false), EUR)
        is BigInteger -> MoneyIO.of(
            BigDecimal.fromBigInteger(this).moveDecimalPoint(2).doubleValue(false), EUR
        )

        else -> error("Unsupported BigNumber type")
    }
val Number.usd: MoneyIO
    get() = MoneyIO.of(
        this.toDouble().toBigDecimal().moveDecimalPoint(2).doubleValue(false), USD
    )
val BigNumber<*>.usd: MoneyIO
    get() = when (this) {
        is BigDecimal -> MoneyIO.of(this.moveDecimalPoint(2).doubleValue(false), USD)
        is BigInteger -> MoneyIO.of(
            BigDecimal.fromBigInteger(this).moveDecimalPoint(2).doubleValue(false), USD
        )

        else -> error("Unsupported BigNumber type")
    }
val EUR: CurrencyIO get() = CurrencyIO.forCode("EUR")
val USD: CurrencyIO get() = CurrencyIO.forCode("USD")

