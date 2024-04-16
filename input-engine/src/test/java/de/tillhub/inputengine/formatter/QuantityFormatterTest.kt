package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.QuantityIO
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class QuantityFormatterTest : FunSpec({

    test("format: Int") {
        QuantityFormatter.format(
            QuantityIO.of(1), locale = Locale.GERMAN
        ) shouldBe "1"

        QuantityFormatter.format(
            QuantityIO.of(10), 2, locale = Locale.GERMAN
        ) shouldBe "10,00"
    }

    test("format: Double") {
        QuantityFormatter.format(
            QuantityIO.of(1.23), locale = Locale.GERMAN
        ) shouldBe "1,23"

        QuantityFormatter.format(
            QuantityIO.of(1.00), locale = Locale.GERMAN
        ) shouldBe "1"
    }

    test("format: BigDecimal") {
        QuantityFormatter.format(
            QuantityIO.of(1.23.toBigDecimal()), locale = Locale.GERMAN
        ) shouldBe "1,23"

        QuantityFormatter.format(
            QuantityIO.of(1.00.toBigDecimal()), locale = Locale.GERMAN
        ) shouldBe "1"
    }
})
