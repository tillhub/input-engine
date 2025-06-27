import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
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
                api(project(":input-engine:financial"))
                implementation(project(":input-engine:formatter"))
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)

                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)

                // Lifecycle
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.lifecycle.viewmodel.compose)

                // Math
                implementation(libs.kotlin.bignum)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test-annotations-common"))

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
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core.v150)
                implementation(libs.androidx.junit.v115)
                implementation(libs.androidx.espresso.core)
                implementation(libs.androidx.ui.test.junit4)
                implementation(libs.kotlinx.serialization.json.v163)
                implementation(libs.compose.ui.test.manifest)
            }
        }

        iosMain {
            dependencies {}
        }
    }
}
compose.resources {
    packageOfResClass = "de.tillhub.inputengine.resources"
    generateResClass = auto
}
