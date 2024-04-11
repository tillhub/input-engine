package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.Money
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Currency
val BigDecimal.eur: Money get() = Money(this, EUR)
val BigDecimal.usd: Money get() = Money(this, USD)
val BigDecimal.gbp: Money get() = Money(this, GBP)
val BigDecimal.jpy: Money get() = Money(this, JPY)
val BigInteger.eur: Money get() = Money.from(this, EUR)
val BigInteger.usd: Money get() = Money.from(this, USD)
val BigInteger.gbp: Money get() = Money.from(this, GBP)
val BigInteger.jpy: Money get() = Money.from(this, JPY)

val EUR: Currency get() = Currency.getInstance("EUR")
val USD: Currency get() = Currency.getInstance("USD")
val GBP: Currency get() = Currency.getInstance("GBP")
val HUF: Currency get() = Currency.getInstance("HUF")
val CHF: Currency get() = Currency.getInstance("CHF")
val JPY: Currency get() = Currency.getInstance("JPY")
