package de.tillhub.inputengine.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PercentIOTest : FunSpec({

    test("constructors: Int") {
        PercentIO.of(1).value shouldBe 100L
        PercentIO.of(56).value shouldBe 5600L
        PercentIO.of(100).value shouldBe 10000L
    }

    test("constructors: Long") {
        PercentIO.of(1L).value shouldBe 100L
        PercentIO.of(56L).value shouldBe 5600L
        PercentIO.of(100L).value shouldBe 10000L
    }

    test("constructors: Double") {
        PercentIO.of(1.0).value shouldBe 100L
        PercentIO.of(5.6).value shouldBe 560L
        PercentIO.of(56.07).value shouldBe 5607L
        PercentIO.of(100.0).value shouldBe 10000L
    }

    test("constructors: BigInteger") {
        PercentIO.of(1.toBigInteger()).value shouldBe 100L
        PercentIO.of(56.toBigInteger()).value shouldBe 5600L
        PercentIO.of(100.toBigInteger()).value shouldBe 10000L
    }

    test("constructors: BigDecimal") {
        PercentIO.of(1.0.toBigDecimal()).value shouldBe 100L
        PercentIO.of(5.6.toBigDecimal()).value shouldBe 560L
        PercentIO.of(56.07.toBigDecimal()).value shouldBe 5607L
        PercentIO.of(100.0.toBigDecimal()).value shouldBe 10000L
    }
})
