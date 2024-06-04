
[![](https://jitpack.io/v/tillhub/input-engine.svg)](https://jitpack.io/#tillhub/input-engine)
[![API](https://img.shields.io/badge/API-24%2B-green.svg?style=flat)](https://android-arsenal.com/api?level-11) 
# Input Engine

Android UI Library which allows easy customisable UI inputs for money, quantity, percentage & PIN.
# How to setup

**Step 1.** Add the JitPack repository to your `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    repositories {
        ...
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency to your app `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.tillhub:input-engine:x.x.x'
}
```
# Usage

Register a callback for an activity result.

In UI component like Activity or Fragment first register one of Input contracts:
* `AmountInputContract` - use for amount (money) input
* `PercentageInputContract` - use for percentage input
* `QuantityInputContract` - use for quantity input
* `PinInputContract` - use for PIN verification

```kotlin
val getAmount = registerForActivityResult(AmountInputContract()) {
    // Handle the returned AmountInputResult response
}

override fun onCreate(savedInstanceState: Bundle?) {
    // ...

    val amountButton = findViewById<Button>(R.id.amount_button)

    amountButton.setOnClickListener {
        // Pass in AmountInputRequest as the input
        getAmount.launch(AmountInputRequest(...))
    }
}
```
Result is sealed class which has two outcomes, success or canceled.

```kotlin
sealed class AmountInputResult {
    class Success(val amount: MoneyIO, val extras: Bundle) : AmountInputResult()
    data object Canceled : AmountInputResult()
}
```

## Credits

- [Đorđe Hrnjez](https://github.com/djordjeh)
- [Martin Širok](https://github.com/SloInfinity)
- [Chandrashekar Allam](https://github.com/shekar-allam)

## License

```licence
MIT License

Copyright (c) 2024 Tillhub GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
