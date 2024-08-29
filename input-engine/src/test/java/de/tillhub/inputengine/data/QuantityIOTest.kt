package de.tillhub.inputengine.data

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger

class QuantityIOTest : DescribeSpec({

    describe("constructors") {
        it("Int") {
            QuantityIO.of(1).value shouldBe 10000.toBigInteger()
            QuantityIO.of(1).getDecimal() shouldBe 1.toBigDecimal()
            QuantityIO.of(25).value shouldBe 250000.toBigInteger()
            QuantityIO.of(10000).value shouldBe 100000000.toBigInteger()
            QuantityIO.of(1).toInt() shouldBe 1
            QuantityIO.of(1).toDouble() shouldBe 1.0
        }
        it("Double") {
            QuantityIO.of(1.00).value shouldBe 10000.toBigInteger()
            QuantityIO.of(25.78).value shouldBe 257800.toBigInteger()
            QuantityIO.of(328.98).value shouldBe 3289800.toBigInteger()
            QuantityIO.of(328.98).toDouble() shouldBe 328.98
            QuantityIO.of(328.98).toInt() shouldBe 328
        }
        it("BigInteger") {
            QuantityIO.of(BigInteger.ONE).value shouldBe 10000.toBigInteger()
            QuantityIO.of(BigInteger.ONE).toInt() shouldBe 1
            QuantityIO.of(BigInteger.TEN).value shouldBe 100000.toBigInteger()
            QuantityIO.of(BigInteger.TEN).toDouble() shouldBe 10.0
        }
        it("BigDecimal") {
            QuantityIO.of(BigDecimal.ONE).value shouldBe 10000.toBigInteger()
            QuantityIO.of(BigDecimal.ONE).toInt() shouldBe 1
            QuantityIO.of(BigDecimal.TEN).value shouldBe 100000.toBigInteger()
            QuantityIO.of(BigDecimal.TEN).toDouble() shouldBe 10.0
        }
    }

    describe("digits") {
        it("majorDigits") {
            QuantityIO.of(123).getMajorDigits() shouldBe listOf(Digit.ONE, Digit.TWO, Digit.THREE)
            QuantityIO.of(98).getMajorDigits() shouldBe listOf(Digit.NINE, Digit.EIGHT)
        }
        it("minorDigits") {
            QuantityIO.of(98).getMinorDigits() shouldBe emptyList()
            QuantityIO.of(98.45).getMinorDigits() shouldBe listOf(Digit.FOUR, Digit.FIVE)
            QuantityIO.of(0.369).getMinorDigits() shouldBe listOf(Digit.THREE, Digit.SIX, Digit.NINE)
            QuantityIO.of(0.3692).getMinorDigits() shouldBe listOf(Digit.THREE, Digit.SIX, Digit.NINE, Digit.TWO)
            QuantityIO.of(0.36921).getMinorDigits() shouldBe listOf(Digit.THREE, Digit.SIX, Digit.NINE, Digit.TWO)
        }
    }

    describe("operations") {
        it("hasFractions") {
            QuantityIO.of(45).hasFractions().shouldBeFalse()
            QuantityIO.of(45.56).hasFractions().shouldBeTrue()
            QuantityIO.of(0.1).hasFractions().shouldBeTrue()
        }
        it("isPositive") {
            QuantityIO.ZERO.isPositive(false).shouldBeFalse()
            QuantityIO.ZERO.isPositive(true).shouldBeTrue()
            QuantityIO.MAX_VALUE.isPositive().shouldBeTrue()
            QuantityIO.MIN_VALUE.isPositive().shouldBeFalse()
        }
        it("isNegative") {
            QuantityIO.ZERO.isNegative().shouldBeFalse()
            QuantityIO.MAX_VALUE.isNegative().shouldBeFalse()
            QuantityIO.MIN_VALUE.isNegative().shouldBeTrue()
        }
        it("isZero") {
            QuantityIO.ZERO.isZero().shouldBeTrue()
            QuantityIO.of(1).isZero().shouldBeFalse()
        }
        it("nextSmaller") {
            QuantityIO.of(4.5).nextSmaller() shouldBe QuantityIO.of(4)
            QuantityIO.of(4).nextSmaller() shouldBe QuantityIO.of(3)
            QuantityIO.of(1.001).nextSmaller() shouldBe QuantityIO.of(1)
            QuantityIO.of(1).nextSmaller() shouldBe QuantityIO.of(1)
            QuantityIO.of(1).nextSmaller(true) shouldBe QuantityIO.ZERO
            QuantityIO.ZERO.nextSmaller(allowsNegatives = true) shouldBe QuantityIO.of(-1)
        }
        it("nextLarger") {
            QuantityIO.of(4.5).nextLarger() shouldBe QuantityIO.of(5)
            QuantityIO.of(4).nextLarger() shouldBe QuantityIO.of(5)
            QuantityIO.of(1.01).nextLarger() shouldBe QuantityIO.of(2)
            QuantityIO.of(-1).nextLarger() shouldBe QuantityIO.of(1)
            QuantityIO.of(-1).nextLarger(true) shouldBe QuantityIO.ZERO
            QuantityIO.MAX_VALUE.nextLarger() shouldBe QuantityIO.MAX_VALUE
        }
    }
})
