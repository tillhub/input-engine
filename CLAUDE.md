# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Input Engine is a Kotlin Multiplatform UI Library that provides customizable input screens for money amounts, percentages, quantities, and PIN entry. It supports both Android and iOS platforms using shared Compose Multiplatform UI code.

## Build Commands

### Building and Testing

```bash
# Build the entire project
./gradlew build

# Build only the input-engine library
./gradlew :input-engine:build

# Run unit tests (Android)
./gradlew testDebug

# Run Android instrumented tests (requires emulator or device)
./gradlew :input-engine:connectedAndroidTest

# Build sample app
./gradlew :sample:assemble
```

### Code Quality

```bash
# Check code formatting (ktlint)
./gradlew spotlessCheck

# Auto-fix formatting issues
./gradlew spotlessApply

# Run Android lint
./gradlew lint
./gradlew lintFix  # Apply safe suggestions
```

### iOS Development

```bash
# Build XCFramework for iOS integration
./gradlew :input-engine:assembleXCFramework
```

Note: iOS builds require Xcode and macOS. The XCFramework will be generated in `input-engine/build/`.

### Publishing

```bash
# Publish to Maven Central (requires credentials)
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

## Architecture

### Multiplatform Structure

The codebase follows standard Kotlin Multiplatform conventions with these source sets:

- **`commonMain/`** - Shared business logic, UI components (Compose Multiplatform), and contracts
- **`androidMain/`** - Android-specific implementations (Activities, Activity Result Contracts)
- **`iosMain/`** - iOS-specific implementations (Presenters, UIViewController bridges)
- **`commonTest/`, `androidUnitTest/`, `iosTest/`** - Platform-specific tests

### Key Package Structure

- **`contract/`** - Platform-agnostic input contracts with `expect`/`actual` implementations
  - Common interfaces define the contract (e.g., `AmountInputContract`)
  - Android uses Activity Result API
  - iOS uses Presenter pattern with `ComposeUIViewController`
  - Legacy Android contracts exist in `androidMain/contract/legacy/` for backward compatibility

- **`data/`** - Data models and I/O types
  - `MoneyIO`, `PercentIO`, `QuantityIO` - Serializable data transfer objects
  - `MoneyParam`, `PercentageParam`, `QuantityParam` - Configuration parameters (Enable/Disable sealed classes)
  - `CurrencyIO` - Currency representation

- **`domain/`** - Domain logic and utilities
  - `NumpadKey`, `Digit` - Input handling abstractions
  - `StringParam` - Sealed class for optional string parameters
  - `helper/` - Business logic helpers

- **`formatting/`** - Platform-specific number formatters
  - Common interfaces with `expect`/`actual` implementations
  - Uses native platform formatting (Android: `NumberFormat`, iOS: `NSNumberFormatter`)

- **`ui/`** - Compose Multiplatform UI layer
  - `screens/` - Full-screen composables (`AmountInputScreen`, `QuantityInputScreen`, etc.)
  - `components/` - Reusable UI components (`NumberButton`, `Toolbar`, etc.)
  - ViewModels in `commonMain` using `androidx.lifecycle`
  - Android Activities bridge to ViewModels
  - iOS Presenters use `ComposeUIViewController` to display screens

- **`theme/`** - Compose theming and styling

### Contract Pattern

The library uses a contract-based API where each input type has:

1. **Request** - Input parameters (e.g., `AmountInputRequest`)
2. **Result** - Sealed class with `Success` or `Canceled` (e.g., `AmountInputResult`)
3. **Contract** - Platform-specific launcher interface

Example flow:
- Android: Uses `rememberLauncherForActivityResult` to launch Activities
- iOS: Uses `AmountInputPresenter` to present `ComposeUIViewController`
- Both platforms share the same Compose UI screens and ViewModels

### ViewModel Factory Pattern

ViewModels are created using factory pattern with `CreationExtras`:
- Request data and formatters are passed via `CreationExtras`
- iOS uses `MutableCreationExtras` in Presenters
- Android Activities retrieve extras from Intent

## Configuration

### Build Configuration

- **Application ID**: `de.tillhub.inputengine`
- **Min SDK**: 24 (Android 7.0)
- **Compile SDK**: 35
- **Java Version**: 17
- **iOS Framework Name**: `input-engineKit`
- **Maven Coordinates**: `io.github.tillhub:input-engine`

### Gradle Plugins

- Kotlin Multiplatform
- Android Library
- Compose Multiplatform & Compose Compiler
- Kotlinx Serialization
- Mokkery (for mocking in tests)
- Spotless (code formatting with ktlint)
- Maven Publishing

## Testing

### Test Exclusions

UI tests are excluded from unit test runs (configured in `input-engine/build.gradle.kts`):
- `**/inputengine/ui/components/**`
- `**/inputengine/ui/screens/**`

These are run separately as instrumented tests on Android or as iOS UI tests.

### Running Specific Tests

```bash
# Run tests for a specific formatter
./gradlew testDebug --tests "*MoneyFormatterTest"

# Run all formatting tests
./gradlew testDebug --tests "*.formatting.*"
```

## CI/CD Workflows

- **PR Checks** (`pr-checks.yml`): Runs `spotlessCheck` and `testDebug` on pull requests
- **Develop Branch** (`develop.yml`): Runs Android instrumented tests on emulator
- **Master Branch** (`publish.yml`): Publishes to Maven Central (macOS runner required for iOS compilation)

## Important Notes

### Serialization

All request/result classes use `kotlinx.serialization` with `@Serializable` annotations. Android passes serialized JSON through Intent extras; iOS passes objects directly through ViewModels.

### Expect/Actual Pattern

Platform-specific implementations are marked with `expect` (common) and `actual` (platform-specific):
- Contracts (`rememberAmountInputLauncher`, etc.)
- Formatters (`MoneyFormatterImpl`, `DecimalFormatter`, etc.)

When modifying these, ensure both Android and iOS implementations are updated.

### Legacy Support

Android maintains legacy Activity Result Contracts in `androidMain/contract/legacy/` for apps that haven't migrated to the Compose-based API. Do not remove these without coordination.
