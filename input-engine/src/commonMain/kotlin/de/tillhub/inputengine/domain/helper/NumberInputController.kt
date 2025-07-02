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
    private val _majorDigits: MutableList<Digit> = mutableListOf(Digit.ZERO)
    private val _minorDigits: MutableList<Digit> = mutableListOf()

    override val minorDigits: List<Digit> get() = _minorDigits

    override fun setValue(majorDigits: List<Digit>, minorDigits: List<Digit>, isNegative: Boolean) {
        this.switchToMinorDigits = minorDigits.isNotEmpty()
        this.switchToNegate = isNegative
        this._majorDigits.clear()
        this._majorDigits.addAll(majorDigits)
        this._minorDigits.clear()
        this._minorDigits.addAll(minorDigits)
    }

    override fun setValue(number: Number) {
        _majorDigits.clear()
        _minorDigits.clear()

        when (number) {
            is Int -> {
                _majorDigits.addAll(DigitBuilder.digits(number.toBigInteger()))
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
        _majorDigits.clear()
        _minorDigits.clear()

        when (number) {
            is BigInteger -> {
                _majorDigits.addAll(DigitBuilder.digits(number))
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

        _majorDigits.addAll(DigitBuilder.digits(integerPart.toBigInteger()))

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
                _majorDigits == listOf(Digit.ZERO) -> {
                    _majorDigits.clear()
                    _majorDigits.add(digit)
                }

                _majorDigits.size < maxMajorDigits -> {
                    _majorDigits.add(digit)
                }
            }
        }
    }

    override fun deleteLast() {
        when {
            _minorDigits.isNotEmpty() -> _minorDigits.removeLast().also {
                switchToMinorDigits = _minorDigits.isNotEmpty()
            }

            _majorDigits.isNotEmpty() -> _majorDigits.removeLast()
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

    override fun value(): Number {
        return if (_minorDigits.isEmpty()) {
            val result = _majorDigits.fold(BigInteger.ZERO) { acc, digit ->
                acc * BigInteger.TEN + digit.value.toBigInteger()
            }.let { if (switchToNegate) it.negate() else it }

            result.longValue(false)
        } else {
            val totalDigits = _majorDigits + _minorDigits
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
    }

    override fun clear() {
        _majorDigits.clear()
        _majorDigits.add(Digit.ZERO)
        _minorDigits.clear()
        switchToMinorDigits = false
        switchToNegate = false
    }

    companion object {
        private const val DEFAULT_DECIMAL_PRECISION = 34L
    }
}
