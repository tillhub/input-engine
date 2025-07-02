import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    compilerOptions {
        // removes warnings for expect/actual classes
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)

        dependencies {
            androidTestImplementation(libs.androidx.ui.test.junit4.android)
            androidTestImplementation(libs.androidx.ui.test.manifest)
        }
    }

    val xcfName = "input-engineKit"

    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain {
            dependencies {
                // Kotlin
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
                implementation(kotlin("test"))

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
    }
}

android {
    namespace = Configs.APPLICATION_ID
    compileSdk = Configs.COMPILE_SDK
    defaultConfig {
        minSdk = Configs.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = Configs.JAVA_VERSION
        targetCompatibility = Configs.JAVA_VERSION
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }

    testOptions {
        unitTests {
            all {
                // We want to exclude all UI tests from the unit tests
                it.exclude("**/inputengine/ui/**")
            }
        }
    }
}

compose.resources {
    packageOfResClass = "de.tillhub.inputengine.resources"
    generateResClass = auto
}
