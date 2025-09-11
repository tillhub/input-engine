package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import de.tillhub.inputengine.domain.Digit

internal interface NumberInputController {
    val minorDigits: List<Digit>
    fun addDigit(digit: Digit)
    fun deleteLast()
    fun clear()
    fun switchToMinor(switch: Boolean)
    fun switchNegate()
    fun setValue(majorDigits: List<Digit>, minorDigits: List<Digit>, isNegative: Boolean)
    fun setValue(number: Number)
    fun setValue(number: BigNumber<*>)
    fun value(): Number
}

internal class NumberInputControllerImpl(
    private val maxMajorDigits: Int = 8,
    private val maxMinorDigits: Int = 4,
) : NumberInputController {
    private var switchToMinorDigits = false
    private var switchToNegate = false

    private val majorDigits: MutableList<Digit> = mutableListOf(Digit.ZERO)
    override val minorDigits: List<Digit> get() = _minorDigits

    private val _minorDigits: MutableList<Digit> = mutableListOf()

    override fun setValue(majorDigits: List<Digit>, minorDigits: List<Digit>, isNegative: Boolean) {
        this.switchToMinorDigits = minorDigits.isNotEmpty()
        this.switchToNegate = isNegative
        this.majorDigits.clear()
        this.majorDigits.addAll(majorDigits)
        this._minorDigits.clear()
        this._minorDigits.addAll(minorDigits)
    }

    override fun setValue(number: Number) {
        majorDigits.clear()
        _minorDigits.clear()

        when (number) {
            is Int -> {
                majorDigits.addAll(DigitBuilder.digits(number.toBigInteger()))
            }

            is Double, is Float -> {
                val decimal = number.toString().toBigDecimal()
                applyBigDecimal(decimal)
            }
        }

        switchToMinorDigits = _minorDigits.isNotEmpty()
        switchToNegate = number.toDouble() < 0.0
    }

    override fun setValue(number: BigNumber<*>) {
        majorDigits.clear()
        _minorDigits.clear()

        when (number) {
            is BigInteger -> {
                majorDigits.addAll(DigitBuilder.digits(number))
                switchToNegate = number < 0.0
            }

            is BigDecimal -> {
                val decimal = number.toString().toBigDecimal()
                applyBigDecimal(decimal)
                switchToNegate = number < 0.0
            }
        }
        switchToMinorDigits = _minorDigits.isNotEmpty()
    }

    private fun applyBigDecimal(decimal: BigDecimal) {
        val integerPart = decimal.floor()
        val fractionalPart = decimal - integerPart

        majorDigits.addAll(DigitBuilder.digits(integerPart.toBigInteger()))

        val scaledMinor = fractionalPart
            .roundToDigitPositionAfterDecimalPoint(
                digitPosition = maxMinorDigits.toLong(),
                roundingMode = RoundingMode.TOWARDS_ZERO,
            )
            .multiply(BigDecimal.TEN.pow(maxMinorDigits.toLong()))
            .floor()
            .toBigInteger()

        _minorDigits.addAll(DigitBuilder.minorDigits(scaledMinor, maxMinorDigits))
    }

    override fun addDigit(digit: Digit) {
        if (switchToMinorDigits) {
            if (_minorDigits.size < maxMinorDigits) {
                _minorDigits.add(digit)
            }
        } else {
            when {
                majorDigits == listOf(Digit.ZERO) -> {
                    majorDigits.clear()
                    majorDigits.add(digit)
                }

                majorDigits.size < maxMajorDigits -> {
                    majorDigits.add(digit)
                }
            }
        }
    }

    override fun deleteLast() {
        when {
            _minorDigits.isNotEmpty() -> _minorDigits.removeAt(_minorDigits.lastIndex).also {
                switchToMinorDigits = _minorDigits.isNotEmpty()
            }

            majorDigits.isNotEmpty() -> majorDigits.removeAt(majorDigits.lastIndex)
        }
    }

    override fun switchToMinor(switch: Boolean) {
        this.switchToMinorDigits = switch
        if (!switch) {
            _minorDigits.clear()
        }
    }

    override fun switchNegate() {
        switchToNegate = !switchToNegate
    }

    override fun value(): Number = if (_minorDigits.isEmpty()) {
        val result = majorDigits.fold(BigInteger.ZERO) { acc, digit ->
            acc * BigInteger.TEN + digit.value.toBigInteger()
        }.let { if (switchToNegate) it.negate() else it }

        result.longValue(false)
    } else {
        val totalDigits = majorDigits + _minorDigits
        val scale = _minorDigits.size

        val combined = totalDigits.fold(BigDecimal.ZERO) { acc, digit ->
            acc * BigDecimal.TEN + digit.value.toBigDecimal()
        }

        val scaled = combined
            .divide(
                BigDecimal.TEN.pow(scale),
                decimalMode = DecimalMode(DEFAULT_DECIMAL_PRECISION),
            )
            .let { if (switchToNegate) it.negate() else it }

        scaled.doubleValue(false)
    }

    override fun clear() {
        majorDigits.clear()
        majorDigits.add(Digit.ZERO)
        _minorDigits.clear()
        switchToMinorDigits = false
        switchToNegate = false
    }

    companion object {
        private const val DEFAULT_DECIMAL_PRECISION = 34L
    }
}
