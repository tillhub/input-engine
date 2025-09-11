package de.tillhub.inputengine

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.data.CurrencyIO
import de.tillhub.inputengine.data.MoneyIO

val Number.eur: MoneyIO get() = MoneyIO.of(this.toDouble().toBigDecimal().moveDecimalPoint(2), EUR)
val BigNumber<*>.eur: MoneyIO
    get() =
        when (this) {
            is BigDecimal -> MoneyIO.of(this.moveDecimalPoint(2), EUR)
            is BigInteger -> MoneyIO.of(BigDecimal.fromBigInteger(this).moveDecimalPoint(2), EUR)
            else -> error("Unsupported BigNumber type")
        }
val Number.usd: MoneyIO
    get() = MoneyIO.of(this.toDouble().toBigDecimal().moveDecimalPoint(2), USD)
val BigNumber<*>.usd: MoneyIO
    get() =
        when (this) {
            is BigDecimal -> MoneyIO.of(this.moveDecimalPoint(2), USD)
            is BigInteger -> MoneyIO.of(BigDecimal.fromBigInteger(this).moveDecimalPoint(2), USD)
            else -> error("Unsupported BigNumber type")
        }
val EUR: CurrencyIO get() = CurrencyIO.forCode("EUR")
val USD: CurrencyIO get() = CurrencyIO.forCode("USD")
