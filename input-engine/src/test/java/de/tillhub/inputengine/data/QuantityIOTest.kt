package de.tillhub.inputengine.data

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger

class QuantityIOTest : DescribeSpec({

    describe("constructors") {
        it("Int") {
            QuantityIO.of(1).value shouldBe 100.toBigInteger()
            QuantityIO.of(1).toInt() shouldBe 1
            QuantityIO.of(1).decimal shouldBe 1.toBigDecimal()
            QuantityIO.of(25).value shouldBe 2500.toBigInteger()
            QuantityIO.of(10000).value shouldBe 1000000.toBigInteger()
        }
        it("Double") {
            QuantityIO.of(1.00).value shouldBe 100.toBigInteger()
            QuantityIO.of(25.78).value shouldBe 2578.toBigInteger()
            QuantityIO.of(328.98).value shouldBe 32898.toBigInteger()
        }
        it("BigInteger") {
            QuantityIO.of(BigInteger.ONE).value shouldBe 100.toBigInteger()
            QuantityIO.of(BigInteger.ONE).toInt() shouldBe 1
            QuantityIO.of(BigInteger.TEN).value shouldBe 1000.toBigInteger()
            QuantityIO.of(BigInteger.TEN).toDouble() shouldBe 10.0
        }
        it("BigDecimal") {
            QuantityIO.of(BigDecimal.ONE).value shouldBe 100.toBigInteger()
            QuantityIO.of(BigDecimal.ONE).toInt() shouldBe 1
            QuantityIO.of(BigDecimal.TEN).value shouldBe 1000.toBigInteger()
            QuantityIO.of(BigDecimal.TEN).toDouble() shouldBe 10.0
        }
    }
})
