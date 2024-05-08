package de.tillhub.inputengine.data

import de.tillhub.inputengine.helper.EUR
import de.tillhub.inputengine.helper.eur
import de.tillhub.inputengine.helper.jpy
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class MoneyIOTest : DescribeSpec({

    describe("constructors") {
        it("Int") {
            MoneyIO.of(1_56, EUR).amount shouldBe 1.56.toBigDecimal()
            MoneyIO.of(78_94, EUR).amount shouldBe 78.94.toBigDecimal()
            MoneyIO.of(100, EUR).toInt() shouldBe 100
            MoneyIO.of(100, EUR).toLong() shouldBe 100L
            MoneyIO.of(100, EUR).toDouble() shouldBe 100.0
            MoneyIO.of(100, EUR).toFloat() shouldBe 100.0f
        }
        it("Double") {
            MoneyIO.of(178.56, EUR).amount shouldBe 1.7856.toBigDecimal()
            MoneyIO.of(178.56, EUR).toDouble() shouldBe 178.56
            MoneyIO.of(178.56, EUR).toInt() shouldBe 178
        }
        it("BigDecimal") {
            MoneyIO.of(178.56.toBigDecimal(), EUR).amount shouldBe 1.7856.toBigDecimal()
            MoneyIO.of(178.56.toBigDecimal(), EUR).toDouble() shouldBe 178.56
            MoneyIO.of(178.56.toBigDecimal(), EUR).toInt() shouldBe 178
        }
        it("BigInteger") {
            MoneyIO.of(1_56.toBigInteger(), EUR).amount shouldBe 1.56.toBigDecimal()
            MoneyIO.of(78_94.toBigInteger(), EUR).toInt() shouldBe 7894
        }
    }

    describe("Money is zero") {
        it("should check correctly") {
            MoneyIO.zero(EUR).isZero().shouldBeTrue()
            MoneyIO.zero(EUR).isNotZero().shouldBeFalse()

            MoneyIO.of(BigInteger.ZERO, EUR).isZero().shouldBeTrue()
            MoneyIO.of(BigInteger.ZERO, EUR).isNotZero().shouldBeFalse()
        }
    }
    describe("isNegative") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isNegative().shouldBeTrue()
            10.toBigInteger().eur.isNegative().shouldBeFalse()

            (-10.0).toBigDecimal().eur.isNegative().shouldBeTrue()
            10.0.toBigDecimal().eur.isNegative().shouldBeFalse()
        }
    }
    describe("Money is positive") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isPositive().shouldBeFalse()
            10.toBigInteger().eur.isPositive().shouldBeTrue()
            0.toBigInteger().eur.isPositive(true).shouldBeTrue()

            (-10.0).toBigDecimal().eur.isPositive().shouldBeFalse()
            10.0.toBigDecimal().eur.isPositive().shouldBeTrue()
            0.toBigDecimal().eur.isPositive().shouldBeFalse()
        }
    }

    describe("Money is isValid") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isValid().shouldBeTrue()
            10.toBigInteger().eur.isValid().shouldBeTrue()
            1000000001.toBigInteger().eur.isValid().shouldBeFalse()

            (-1000000001.0).toBigDecimal().eur.isValid().shouldBeFalse()
            10.0.toBigDecimal().eur.isValid().shouldBeTrue()
            10000001.toBigDecimal().eur.isValid().shouldBeFalse()
        }
    }

    describe("negate") {
        it("should negate amount") {
            10.eur.negate() shouldBe (-10).eur
            (-6).eur.negate() shouldBe 6.eur
        }
    }

    describe("abs") {
        it("should return absolute amount") {
            (-6).eur.abs() shouldBe 6.eur
            6.eur.abs() shouldBe 6.eur
        }
    }

    describe("Money append") {
        it("should check correctly") {
            MoneyIO.append(1.toBigInteger().eur, Digit.ONE) shouldBe 0.11.toBigDecimal().eur
            MoneyIO.append((-1).toBigInteger().eur, Digit.ONE) shouldBe (-0.11).toBigDecimal().eur
            MoneyIO.append(1.toBigDecimal().eur, Digit.ONE) shouldBe 10.01.toBigDecimal().eur
            MoneyIO.append(
                1000000001.toBigInteger().eur,
                Digit.ONE
            ) shouldBe 1000000001.toBigInteger().eur
        }
    }
    describe("compareTo") {
        it("should throw exception") {
            shouldThrow<IllegalArgumentException> {
                10.toBigInteger().eur.compareTo(10.toBigInteger().jpy)
            }
            shouldThrow<IllegalArgumentException> {
                10.0.toBigDecimal().eur.compareTo(10.toBigDecimal().jpy)
            }
        }

        it("should be less") {
            ((-10).toBigInteger().eur < (-9).toBigInteger().eur).shouldBeTrue()
            (9.toBigInteger().eur < 10.toBigInteger().eur).shouldBeTrue()

            ((-10.00).toBigDecimal().eur < (-9.00).toBigDecimal().eur).shouldBeTrue()
            (10.00.toBigDecimal().eur < 10.01.toBigDecimal().eur).shouldBeTrue()
            (1.23456788.toBigDecimal().eur < 1.23456789.toBigDecimal().eur).shouldBeTrue()
        }

        it("should be equal") {
            ((-10).toBigInteger().eur == (-10).toBigInteger().eur).shouldBeTrue()
            (10.toBigInteger().eur == 10.toBigInteger().eur).shouldBeTrue()

            ((-10.00).toBigDecimal().eur == (-10.00).toBigDecimal().eur).shouldBeTrue()
            (10.00.toBigDecimal().eur == 10.00.toBigDecimal().eur).shouldBeTrue()
            (1.23456789.toBigDecimal().eur == 1.23456789.toBigDecimal().eur).shouldBeTrue()
        }

        it("should be greater") {
            ((-10).toBigInteger().eur < (-9).toBigInteger().eur).shouldBeTrue()
            (9.toBigInteger().eur < 10.toBigInteger().eur).shouldBeTrue()

            ((-9.00).toBigDecimal().eur > (-10.00).toBigDecimal().eur).shouldBeTrue()
            (10.01.toBigDecimal().eur > 10.00.toBigDecimal().eur).shouldBeTrue()
            (1.23456789.toBigDecimal().eur > 1.23456788.toBigDecimal().eur).shouldBeTrue()
        }
    }
    describe("removeLastMinorDigit") {
        it("should remove last minor digit") {
            MoneyIO.removeLastDigit(0.01.toBigDecimal().eur) shouldBe 0.toBigInteger().eur
            MoneyIO.removeLastDigit(11.toBigInteger().eur) shouldBe 1.toBigInteger().eur
        }
    }
})
