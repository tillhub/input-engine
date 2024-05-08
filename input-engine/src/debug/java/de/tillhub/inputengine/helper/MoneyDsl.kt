package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.MoneyIO
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Currency

val BigDecimal.eur: MoneyIO get() = MoneyIO(this, EUR)
val BigDecimal.usd: MoneyIO get() = MoneyIO(this, USD)
val BigDecimal.gbp: MoneyIO get() = MoneyIO(this, GBP)
val BigDecimal.jpy: MoneyIO get() = MoneyIO(this, JPY)
val BigInteger.eur: MoneyIO get() = MoneyIO.of(this, EUR)
val BigInteger.usd: MoneyIO get() = MoneyIO.of(this, USD)
val BigInteger.gbp: MoneyIO get() = MoneyIO.of(this, GBP)
val BigInteger.jpy: MoneyIO get() = MoneyIO.of(this, JPY)

val Int.eur: MoneyIO get() = MoneyIO.of(this, EUR)

val EUR: Currency get() = Currency.getInstance("EUR")
val USD: Currency get() = Currency.getInstance("USD")
val GBP: Currency get() = Currency.getInstance("GBP")
val HUF: Currency get() = Currency.getInstance("HUF")
val CHF: Currency get() = Currency.getInstance("CHF")
val JPY: Currency get() = Currency.getInstance("JPY")
