package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.MoneyIO
import de.tillhub.inputengine.helper.EUR
import de.tillhub.inputengine.helper.eur
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class MoneyFormatterTest : FunSpec({

    test("format: Int") {
        MoneyFormatter.format(
            MoneyIO.of(1_56, EUR), Locale.GERMAN
        ) shouldBe "1,56 €"
    }

    test("format: Double") {
        MoneyFormatter.format(
            MoneyIO.of(100.0, EUR), Locale.GERMAN
        ) shouldBe "1,00 €"
    }

    test("format: BigInteger") {
        MoneyFormatter.format(
            MoneyIO.of(100.toBigInteger(), EUR), Locale.GERMAN
        ) shouldBe "1,00 €"
    }

    test("zero with big decimal") {
        MoneyFormatter.format(
            MoneyIO.of(0.0.toBigDecimal(), EUR), Locale.GERMAN
        ) shouldBe "0,00 €"
    }

    test("zero with big integer") {
        MoneyFormatter.format(
            MoneyIO.of(0.toBigInteger(), EUR), Locale.GERMAN
        ) shouldBe "0,00 €"
    }
})
