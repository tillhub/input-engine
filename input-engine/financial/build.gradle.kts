plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // Enable expect/actual class support
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // Android target setup
    androidLibrary {
        namespace = Configs.APPLICATION_ID + ".financial"
        compileSdk = Configs.COMPILE_SDK
        minSdk = Configs.MIN_SDK
    }

    val xcfName = "financialKit"
    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.bignum)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
