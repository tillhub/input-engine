import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    // Enable expect/actual class support
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = Configs.APPLICATION_ID
        compileSdk = Configs.COMPILE_SDK
        minSdk = Configs.MIN_SDK
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true

        @Suppress("UnstableApiUsage")
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "input-engineKit"

    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain {
            dependencies {
                // Project modules
                api(project(":input-engine:financial"))
                implementation(project(":input-engine:formatter"))

                // Kotlin
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.bignum)

                // Compose Multiplatform
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.runtime)
                implementation(compose.ui)

                // Lifecycle
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.lifecycle.viewmodel.compose)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)

                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.activity.ktx)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
                implementation(libs.androidx.runner)
                implementation(libs.androidx.ui.test.junit4)
                implementation(libs.compose.ui.test.manifest)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here if needed
            }
        }
    }
}

compose.resources {
    packageOfResClass = "de.tillhub.inputengine.resources"
    generateResClass = auto
}
