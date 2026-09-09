# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Input Engine is a Kotlin Multiplatform library (Android + iOS) that provides full-screen numpad input screens for
money amounts, percentages, quantities and PIN entry. UI is Compose Multiplatform in `commonMain`; each platform only
contributes a launcher (Android Activity / iOS `UIViewController` presenter) and number formatters.

Published to Maven Central as `io.github.tillhub:input-engine`. README.md is the consumer-facing documentation and
documents the public API in detail; keep it in sync when changing requests, results or value types.

## Commands

```bash
./gradlew build                                   # build all modules
./gradlew spotlessCheck                           # ktlint (CI gate); ./gradlew spotlessApply to fix
./gradlew testDebug                               # unit tests: commonTest + androidUnitTest (what PR CI runs)
./gradlew testDebug --tests "*MoneyFormatterTest" # single test class
./gradlew testDebug --tests "*.formatting.*"      # package
./gradlew :input-engine:iosSimulatorArm64Test     # iOS unit tests (commonTest + iosTest, needs Xcode)
./gradlew :input-engine:connectedAndroidTest      # instrumented tests (emulator/device; runs on develop CI)
./gradlew :sample:installDebug                    # sample Android app
./gradlew lint                                    # Android lint
```

iOS framework: there is **no** `assembleXCFramework` task. Per-target frameworks come from
`:input-engine:linkReleaseFrameworkIosArm64` / `linkReleaseFrameworkIosSimulatorArm64` (output in
`input-engine/build/bin/<target>/releaseFramework/input-engineKit.framework`). The `iosApp` Xcode project embeds the
`sample` module via `:sample:embedAndSignAppleFrameworkForXcode`.

Publishing: `./gradlew publishAndReleaseToMavenCentral --no-configuration-cache` (configuration cache is on globally in
`gradle.properties` and must be disabled for this task). CI does this when a version tag is pushed, see below.

## Tests

- Compose UI tests live in `commonTest` under `ui/components/**` and `ui/screens/**`. They are **excluded from
  `testDebug`** (see `testOptions.unitTests` in `input-engine/build.gradle.kts`) and only run as Android instrumented
  tests or iOS native tests. ViewModel, data and domain tests in `commonTest` do run on the JVM.
- UI tests use `runCustomComposeUiTest` (`testing/ComposeUiTest.kt`), an `expect` with an `actual` in each of
  `androidUnitTest`, `androidInstrumentedTest` and `iosTest`. Add an `actual` if a new test source set is introduced.
- `commonTest/MoneyDsl.kt` provides `100.0.eur`, `5.usd`, `EUR`, `USD` helpers; `Number.eur` treats the receiver as
  major units.
- Mokkery is used only in the `ui/screens` tests to mock formatters.
- Formatter tests are platform-specific (`androidUnitTest/formatting`, `iosTest/formatting`) because output depends on
  `NumberFormat` / `NSNumberFormatter`.

## Architecture

### Request → Screen → Result flow

Each input type (Amount, Percentage, Quantity, Pin) has the same four pieces:

1. `commonMain/contract/<X>InputContract.kt`: `@Serializable` `<X>InputRequest`, sealed `<X>InputResult`
   (`Success` / `Canceled`), the `<X>InputContract` launcher interface, and
   `@Composable expect fun remember<X>InputLauncher(onResult)`.
2. `commonMain/ui/<X>InputViewModel.kt` (internal): all input logic. Built via `viewModelFactory` with
   `CreationExtras` keys `REQUEST_KEY` and (except Pin) `FORMATTER_KEY`.
3. `commonMain/ui/screens/<X>InputScreen.kt` (internal): Compose UI wrapped in `AppTheme`, takes the ViewModel plus
   `onResult` / `onDismiss` callbacks.
4. Platform host:
   - **Android** `androidMain/ui/<X>InputActivity.kt`: reads the request as JSON from the Intent extra
     `ExtraKeys.EXTRAS_REQUEST`, returns `Success` as JSON in `ExtraKeys.EXTRAS_RESULT` with `RESULT_OK`, or
     `RESULT_CANCELED`. `androidMain/contract/<X>InputContract.android.kt` implements the `actual` launcher with
     `rememberLauncherForActivityResult` and exposes an internal `parse<X>InputResult(resultCode, extras)`.
   - **iOS** `iosMain/ui/<X>InputPresenter.kt`: wraps the screen in `ComposeUIViewController` and presents it on
     `UIApplication.sharedApplication.keyWindow?.rootViewController` (silently no-ops if there is none). The `actual`
     launcher in `iosMain/contract/` just delegates to the presenter. No serialization on iOS.

`androidMain/contract/legacy/` holds plain `ActivityResultContract` classes for non-Compose consumers. They reuse the
same Activities and the shared `parse*InputResult` functions, so a change to the Intent protocol must keep both paths
working. Do not remove them without coordination.

Activities are declared in `androidMain/AndroidManifest.xml` as non-exported; a new input type needs an entry there.

### Input logic

- **Amount** appends digits cash-register style via `MoneyIO.append` (value shifts left by one digit, new digit fills
  the smallest currency unit). It never uses the decimal separator. `AmountInputViewModel.setupAmountConstraints`
  normalizes min/max into an `AmountInputMode` (POSITIVE / NEGATIVE / BOTH); in NEGATIVE mode bounds are flipped to
  positive internally and the result is negated on output. `min >= max` disables both bounds.
- **Percentage** and **Quantity** type into `domain/helper/NumberInputController` (major digits, minor digits, negate
  flag, `value()` returns `Long` or `Double`), then convert with `PercentIO.of` / `QuantityIO.of`. Percentage uses
  `maxMajorDigits = 3`, Quantity `5`. Out-of-range input clears the controller and snaps to the bound (Percentage
  clamps only to max while typing; Quantity clamps to both min and max).
- **Quantity** stepper (`increase` / `decrease`) uses `QuantityIO.nextLarger` / `nextSmaller`, which round fractional
  values to the next whole number and respect `allowsZero` / negative min.
- **Pin** compares the typed digits to `request.pin`. Same-length mismatch shows a snackbar and clears; match returns
  `Success(extras)` without the PIN. Empty or non-digit `pin` yields `Canceled` immediately.
- Hints: `hintAmount` / `hintQuantity` are shown (as `isHint = true`) only while the current value is zero.

### Value types (`data/`)

`MoneyIO`, `PercentIO`, `QuantityIO` extend `Number` and `Comparable`, have internal/private constructors and factory
`of(...)` overloads for both Kotlin numbers and `com.ionspin.kotlin.bignum` types. Scaling conventions:

- `MoneyIO.of(Int|Long|Double, currency)` interprets the number as **minor units** (`100` → 1.00 EUR);
  `fromMajorUnits(BigDecimal, currency)` does not scale. `amount` is stored in major units. Range ±10 000 000.
- `PercentIO.value` is a `Long` scaled by 100 (`5600` = 56 %). `WHOLE` = 100 %.
- `QuantityIO.value` is a `BigInteger` scaled by 10 000 (`FRACTIONS = 4`). Range ±10 000.
- `CurrencyIO` is a fixed ISO 4217 table; use `forCode` / `forCodeOrNull`.

Optional parameters are sealed `Enable(value)` / `Disable` wrappers (`MoneyParam`, `PercentageParam`,
`QuantityParam`, `StringParam`) rather than nullables so they serialize with kotlinx.serialization. BigDecimal /
BigInteger fields use the custom serializers in `domain/serializer/`.

### expect/actual pairs

Changing any of these requires touching `commonMain`, `androidMain` and `iosMain`:
`remember*InputLauncher`, `MoneyFormatterImpl`, `PercentageFormatterImpl`, `QuantityFormatterImpl`,
`DecimalFormatter` (locale decimal/grouping separators, used for the keyboard's separator key label). The compiler flag
`-Xexpect-actual-classes` is enabled for these.

### Theme and layout

- `theme/AppTheme` is always invoked with the default `useDarkTheme = false`, so screens render the light scheme
  regardless of system setting. This was decided deliberately in PR #30 (dark mode hotfix); do not wire
  `isSystemInDarkTheme()` back in without asking.
- `ui/components/TabletExtensions.kt`: window width ≥ 600 dp switches the Scaffold to `TabletScaffoldModifier`
  (380 dp wide centered card).
- Compose resources (`composeResources/`) generate `de.tillhub.inputengine.resources.Res`; strings exist in English
  and German (`values-de`). Font is Inter.

## Build configuration

- Library version is `input-engine` in `gradle/libs.versions.toml`. `buildSrc/Configs.kt` `VERSION_NAME` /
  `VERSION_CODE` are for the sample app only. SDK levels and Java version also come from `Configs`.
- Spotless/ktlint is applied to all subprojects from the root `build.gradle.kts`, with
  `ktlint_function_naming_ignore_when_annotated_with = Composable`.
- Root project name is `Tillhub_Input_Engine`; modules are `:input-engine` (library) and `:sample` (KMP demo app,
  Android application + iOS framework `ComposeApp` consumed by `iosApp/`).

## Git workflow and CI

The workflows are intentionally identical to the other engine repos (payment-engine, print-engine, scan-engine);
change them in lockstep.

- `pr-checks.yml`: every pull request runs `spotlessCheck` + `testDebug`.
- `master.yml`: push to `master` runs the same checks plus `:input-engine:connectedAndroidTest` on an API 30 emulator.
- `release.yml`: pushing a tag matching `X.Y.Z` publishes to Maven Central (macOS runner, Xcode for iOS targets). It
  first asserts the tag equals the `input-engine` version in `gradle/libs.versions.toml` and fails otherwise.
- `checkmarx-one-scan.yaml`: Unzer Checkmarx security scan on PRs and `master`, via the shared
  `unzercorp/unzer-tech-toolbox` workflow.

Release procedure: bump `input-engine` in `libs.versions.toml`, merge to `master`, then tag that commit with the same
version (`git tag 2.1.4 && git push origin 2.1.4`). Merging alone no longer publishes.

Code owner: `@tillhub/unifiedpos-android-cm`.
