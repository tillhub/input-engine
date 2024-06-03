package de.tillhub.inputengine.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class PercentIOTest : FunSpec({

    test("constructors: Int") {
        PercentIO.of(0) shouldBe PercentIO.ZERO
        PercentIO.of(1).value shouldBe 100L
        PercentIO.of(56).value shouldBe 5600L
        PercentIO.of(100).value shouldBe 10000L
        PercentIO.of(100) shouldBe PercentIO.WHOLE
        PercentIO.of(56).toInt() shouldBe 56
        PercentIO.of(56).toDouble() shouldBe 56.0
        PercentIO.of(56).toRatio() shouldBe 0.56
    }

    test("constructors: Long") {
        PercentIO.of(1L).value shouldBe 100L
        PercentIO.of(56L).value shouldBe 5600L
        PercentIO.of(100L).value shouldBe 10000L
        PercentIO.of(56L).toLong() shouldBe 56L
    }

    test("constructors: Double") {
        PercentIO.of(1.0).value shouldBe 100L
        PercentIO.of(5.6).value shouldBe 560L
        PercentIO.of(56.07).value shouldBe 5607L
        PercentIO.of(100.0).value shouldBe 10000L
        PercentIO.of(56.07).toDouble() shouldBe 56.07
        PercentIO.of(56.07).toRatio() shouldBe 0.5607
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

    test("isNotZero") {
        PercentIO.ZERO.isNotZero().shouldBeFalse()
        PercentIO.WHOLE.isNotZero().shouldBeTrue()
    }
})
