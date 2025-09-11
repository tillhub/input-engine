
[![](https://jitpack.io/v/tillhub/input-engine.svg)](https://jitpack.io/#tillhub/input-engine)
[![API](https://img.shields.io/badge/API-24%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=24)

# Input Engine – Kotlin Multiplatform

**Input Engine** is a Kotlin Multiplatform UI Library that enables easy and customizable UI inputs for:

- 💰 Money
- 📈 Percentage
- 🔢 Quantity
- 🔐 PIN

Now supports **both Android and iOS** via KMP shared code.

---

## 🔧 Setup

### 📦 Android (via JitPack)

**Step 1.** Add the JitPack repository to your root `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency to your `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.tillhub:input-engine:x.x.x'
}
```

---

### 🍏 iOS

**Step 1.** Add the XCFramework to your Xcode project:

- Clone the repo and run:
  ```bash
  ./gradlew :input-engine:assembleXCFramework
  ```
- Drag `InputEngineKit.xcframework` into your Xcode project
- Link it under "Frameworks, Libraries, and Embedded Content"

**Step 2.** Import and use in Swift:

```swift
import InputEngineKit

let contract = AmountInputContract()
// Use Kotlin/Native bridge to integrate with shared logic
```

> ✅ Compatible with both SwiftUI and UIKit.

---

## 🚀 Usage

### Android Example

In your Activity or Fragment:

```kotlin
val getAmount = registerForActivityResult(AmountInputContract()) {
    when (it) {
        is AmountInputResult.Success -> {
            val amount = it.amount
            // Handle the returned amount
        }
        AmountInputResult.Canceled -> {
            // Handle cancellation
        }
    }
}

amountButton.setOnClickListener {
    getAmount.launch(AmountInputRequest(...))
}
```

---

## 🎯 Supported Input Contracts

| Contract                   | Description                          |
|----------------------------|--------------------------------------|
| `AmountInputContract`      | Input for currency / money values    |
| `PercentageInputContract`  | Input for percentage values          |
| `QuantityInputContract`    | Input for decimal/integer quantities |
| `PinInputContract`         | Secure PIN entry                     |

All result contracts follow the sealed structure:

```kotlin
sealed class AmountInputResult {
    class Success(val amount: MoneyIO, val extras: Bundle) : AmountInputResult()
    data object Canceled : AmountInputResult()
}
```

---

## 👥 Contributors

- [Đorđe Hrnjez](https://github.com/djordjeh)
- [Martin Širok](https://github.com/SloInfinity)
- [Chandrashekar Allam](https://github.com/shekar-allam)

---

## 🪪 License

```text
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
