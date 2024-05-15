package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.MoneyIO
import java.util.Currency

val Number.eur: MoneyIO get() = MoneyIO.of(this.toDouble().toBigDecimal().movePointRight(2), EUR)
val Number.usd: MoneyIO get() = MoneyIO.of(this.toDouble().toBigDecimal().movePointRight(2), USD)

val EUR: Currency get() = Currency.getInstance("EUR")
val USD: Currency get() = Currency.getInstance("USD")
