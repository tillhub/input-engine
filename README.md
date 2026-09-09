[![Maven Central](https://img.shields.io/maven-central/v/io.github.tillhub/input-engine.svg)](https://central.sonatype.com/artifact/io.github.tillhub/input-engine)
[![API](https://img.shields.io/badge/API-24%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=24)

# Input Engine – Kotlin Multiplatform

**Input Engine** is a Kotlin Multiplatform UI library that provides ready-made, full-screen numpad inputs for:

- 💰 Money amounts
- 📈 Percentages
- 🔢 Quantities
- 🔐 PIN entry

The UI is written once in Compose Multiplatform and shared between **Android** and **iOS**. Each input is launched with a
`Request`, and returns a sealed `Result` (`Success` or `Canceled`).

---

## 🔧 Requirements

| | |
|---|---|
| Kotlin | 2.2.0 |
| Compose Multiplatform | 1.8.2 |
| Android | minSdk 24, compileSdk 35, Java 17 |
| iOS targets | `iosArm64`, `iosSimulatorArm64`, `iosX64` |

---

## 📦 Setup

The library is published to **Maven Central** under `io.github.tillhub:input-engine`.

> **Migrating from JitPack?** The coordinates changed from `com.github.tillhub:input-engine` to
> `io.github.tillhub:input-engine`. The JitPack repository is no longer required.

### Kotlin Multiplatform project (recommended)

Add the dependency to your shared module's `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.tillhub:input-engine:2.1.3")
        }
    }
}
```

### Android-only project

```kotlin
dependencies {
    implementation("io.github.tillhub:input-engine:2.1.3")
}
```

The library registers its own Activities (`AmountInputActivity`, `PercentageInputActivity`, `QuantityInputActivity`,
`PinInputActivity`) via manifest merging, so no manifest changes are needed.

### iOS

The library is a Kotlin/Native framework named **`input-engineKit`**. The intended way to use it from iOS is through a
Kotlin Multiplatform shared module that depends on the library (see the KMP setup above) and is embedded into Xcode
with the standard `embedAndSignAppleFrameworkForXcode` build phase. This is exactly how the `sample` module and the
`iosApp` Xcode project in this repository are wired.

If you need a standalone framework, link it per target:

```bash
./gradlew :input-engine:linkReleaseFrameworkIosArm64            # device
./gradlew :input-engine:linkReleaseFrameworkIosSimulatorArm64   # simulator
```

The output is `input-engine/build/bin/<target>/releaseFramework/input-engineKit.framework`. There is no
`assembleXCFramework` task configured in this project.

---

## 🚀 Usage

### Shared Compose code (Android + iOS)

Each input exposes a `remember*InputLauncher` composable that returns a platform-specific launcher. On Android it uses
the Activity Result API under the hood, on iOS it presents a `ComposeUIViewController` on the key window's root view
controller.

```kotlin
@Composable
fun CheckoutScreen() {
    val eur = CurrencyIO.forCode("EUR")

    val amountLauncher = rememberAmountInputLauncher { result ->
        when (result) {
            is AmountInputResult.Success -> {
                val amount: MoneyIO = result.amount
                val extras: Map<String, String> = result.extras
            }
            AmountInputResult.Canceled -> Unit
        }
    }

    Button(onClick = {
        amountLauncher.launchAmountInput(
            AmountInputRequest(
                amount = MoneyIO.of(0, eur),
                amountMax = MoneyParam.Enable(MoneyIO.of(50_00, eur)),   // 50.00 EUR
                hintAmount = MoneyParam.Enable(MoneyIO.of(0, eur)),
                extras = mapOf("orderId" to "42"),
            ),
        )
    }) { Text("Enter amount") }
}
```

The same pattern applies to the other inputs:

```kotlin
val percentageLauncher = rememberPercentageInputLauncher { result -> /* PercentageInputResult */ }
val quantityLauncher   = rememberQuantityInputLauncher { result -> /* QuantityInputResult */ }
val pinLauncher        = rememberPinInputLauncher { result -> /* PinInputResult */ }

percentageLauncher.launchPercentageInput(PercentageInputRequest(percent = PercentIO.ZERO))
quantityLauncher.launchQuantityInput(QuantityInputRequest(quantity = QuantityIO.ZERO))
pinLauncher.launchPinInput(PinInputRequest(pin = "1234"))
```

### Android without Compose (legacy Activity Result contracts)

For Activities or Fragments that do not use Compose, `ActivityResultContract` implementations are provided in the
`de.tillhub.inputengine.contract.legacy` package:

```kotlin
import de.tillhub.inputengine.contract.legacy.AmountInputContract

private val getAmount = registerForActivityResult(AmountInputContract()) { result ->
    when (result) {
        is AmountInputResult.Success -> handle(result.amount, result.extras)
        AmountInputResult.Canceled -> Unit
    }
}

amountButton.setOnClickListener {
    getAmount.launch(AmountInputRequest(amount = MoneyIO.of(0, CurrencyIO.forCode("EUR"))))
}
```

`PercentageInputContract`, `QuantityInputContract` and `PinInputContract` live in the same package.

### iOS outside of Compose

The `remember*InputLauncher` functions are thin wrappers around presenters. If you are not inside a composition you can
use the presenters directly from Kotlin (or from Swift through the shared framework):

```kotlin
val presenter = AmountInputPresenter { result -> /* AmountInputResult */ }
presenter.launch(AmountInputRequest(amount = MoneyIO.of(0, CurrencyIO.forCode("EUR"))))
```

Available presenters: `AmountInputPresenter`, `PercentageInputPresenter`, `QuantityInputPresenter`,
`PinInputPresenter`. They present on `UIApplication.sharedApplication.keyWindow?.rootViewController` and dismiss
themselves when a result is delivered.

---

## 🎯 Inputs

Every request accepts a `toolbarTitle: StringParam` (defaults to a localized title, English and German are bundled) and
an `extras: Map<String, String>` that is passed back untouched in the `Success` result.

### Amount

```kotlin
AmountInputRequest(
    amount: MoneyIO,                              // initial value (required)
    isZeroAllowed: Boolean = false,               // whether 0 can be submitted
    toolbarTitle: StringParam = StringParam.Disable,
    amountMin: MoneyParam = MoneyParam.Disable,   // lower bound, shown as "Min. …"
    amountMax: MoneyParam = MoneyParam.Disable,   // upper bound, shown as "Max. …"
    hintAmount: MoneyParam = MoneyParam.Disable,  // greyed-out value shown while the input is 0
    extras: Map<String, String> = emptyMap(),
)
// → AmountInputResult.Success(amount: MoneyIO, extras) | AmountInputResult.Canceled
```

- Typing is clamped to `[amountMin, amountMax]`; values outside `±10 000 000` are rejected.
- The sign toggle is only shown when both negative and positive values are allowed. If `amountMin`/`amountMax` are both
  ≤ 0 the screen works in negative-only mode and returns negative amounts.
- If `amountMin >= amountMax` both bounds are ignored.

### Percentage

```kotlin
PercentageInputRequest(
    percent: PercentIO = PercentIO.ZERO,
    allowsZero: Boolean = false,
    toolbarTitle: StringParam = StringParam.Disable,
    allowDecimal: Boolean = false,                       // shows the decimal separator key
    percentageMin: PercentageParam = PercentageParam.Disable,   // default 0 %
    percentageMax: PercentageParam = PercentageParam.Disable,   // default 100 %
    extras: Map<String, String> = emptyMap(),
)
// → PercentageInputResult.Success(percent: PercentIO, extras) | PercentageInputResult.Canceled
```

Percentages support up to two decimal places (e.g. `15.12 %`).

### Quantity

```kotlin
QuantityInputRequest(
    quantity: QuantityIO = QuantityIO.ZERO,
    allowsZero: Boolean = false,
    toolbarTitle: StringParam = StringParam.Disable,
    allowDecimal: Boolean = true,                        // shows the decimal separator key
    minQuantity: QuantityParam = QuantityParam.Disable,  // default -10 000; a negative min enables the sign key
    maxQuantity: QuantityParam = QuantityParam.Disable,  // default 10 000
    hintQuantity: QuantityParam = QuantityParam.Disable, // greyed-out value shown while the input is 0
    extras: Map<String, String> = emptyMap(),
)
// → QuantityInputResult.Success(quantity: QuantityIO, extras) | QuantityInputResult.Canceled
```

Quantities support up to four decimal places. The screen also offers `-` / `+` stepper buttons that move to the next
smaller / larger whole number within the bounds.

### PIN

```kotlin
PinInputRequest(
    pin: String,                                  // expected PIN, digits only
    toolbarTitle: StringParam = StringParam.Disable,
    overridePinInput: Boolean = false,            // shows a text link that submits Success without a matching PIN
    extras: Map<String, String> = emptyMap(),
)
// → PinInputResult.Success(extras) | PinInputResult.Canceled
```

- The entered PIN is compared against `pin`. A match returns `Success` immediately; a mismatch of the same length shows
  a "Wrong PIN" snackbar and clears the input.
- If `pin` is empty or contains non-digits the screen returns `Canceled` right away.
- The result intentionally does not contain the PIN itself, only the `extras`.

---

## 🧮 Value types

All types are `@Serializable`, implement `Number` and `Comparable`, and are safe to pass through Intents.

| Type | Precision | Construction |
|---|---|---|
| `CurrencyIO` | ISO 4217 code, fraction digits, numeric code | `CurrencyIO.forCode("EUR")`, `CurrencyIO.forCodeOrNull(...)`, `CurrencyIO.all` |
| `MoneyIO` | currency fraction digits (2 for EUR) | `MoneyIO.of(1_00, eur)` = 1.00 EUR (minor units for `Int`/`Long`/`Double`), `MoneyIO.fromMajorUnits(BigDecimal, eur)`, `MoneyIO.zero(eur)` |
| `PercentIO` | 2 decimal places | `PercentIO.of(56)` = 56 %, `PercentIO.of(5.6)` = 5.6 %, `PercentIO.ZERO`, `PercentIO.WHOLE` (100 %) |
| `QuantityIO` | 4 decimal places | `QuantityIO.of(1)` = ×1, `QuantityIO.of(1.56)` = ×1.56, `QuantityIO.ZERO` |

Big-number arithmetic uses [`com.ionspin.kotlin:bignum`](https://github.com/ionspin/kotlin-multiplatform-bignum);
`MoneyIO.amount` is a `BigDecimal` in major units and `QuantityIO.value` is a `BigInteger` scaled by `10 000`.

Optional parameters use small sealed wrappers instead of nullables so they serialize cleanly:
`MoneyParam`, `PercentageParam`, `QuantityParam` and `StringParam`, each with `Enable(value)` and `Disable`.

---

## 🎨 Appearance

- Screens use a Material 3 theme bundled with the library (Inter font). Currently the light color scheme is always used.
- On windows wider than 600 dp (tablets) the input is rendered as a centered 380 dp card instead of full screen.
- Number formatting follows the device locale via `NumberFormat` on Android and `NSNumberFormatter` on iOS.

---

## 📱 Sample app

The `sample` module is a Compose Multiplatform app that launches all four inputs and prints the result.

```bash
# Android
./gradlew :sample:installDebug

# iOS: open iosApp/iosApp.xcodeproj in Xcode and run the iosApp scheme.
# Xcode invokes ./gradlew :sample:embedAndSignAppleFrameworkForXcode automatically.
```

---

## 🛠 Development

```bash
./gradlew build                                   # build everything
./gradlew spotlessCheck                           # ktlint check (spotlessApply to fix)
./gradlew testDebug                               # Android unit tests (common + androidUnitTest)
./gradlew :input-engine:iosSimulatorArm64Test     # iOS unit tests (requires Xcode)
./gradlew :input-engine:connectedAndroidTest      # Android instrumented tests (emulator/device)
```

Compose UI tests under `ui/components` and `ui/screens` are excluded from `testDebug` and run as instrumented tests
on Android and as native tests on iOS.

### Releasing

1. Bump `input-engine` in `gradle/libs.versions.toml` and merge to `master`.
2. Tag the merge commit with the same version and push the tag:

   ```bash
   git tag 2.1.4 && git push origin 2.1.4
   ```

The `release.yml` workflow verifies that the tag matches the catalog version and publishes to Maven Central.

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
