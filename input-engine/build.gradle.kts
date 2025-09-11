import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.maven.publish)
}

kotlin {
    compilerOptions {
        // removes warnings for expect/actual classes
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)

        publishLibraryVariants("release")

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

        iosTest.dependencies {
            implementation(kotlin("test"))

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
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
                it.exclude(
                    "**/inputengine/ui/components/**",
                    "**/inputengine/ui/screens/**",
                )
            }
        }
    }
}

compose.resources {
    packageOfResClass = "de.tillhub.inputengine.resources"
    generateResClass = auto
}

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "io.github.tillhub",
        artifactId = "input-engine",
        version =
            libs.versions.input.engine
                .get(),
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Input Engine")
        description.set("Kotlin MultiPlatform Library which allows easy customisable UI inputs for money, quantity, percentage & PIN.")
        inceptionYear.set("2025")
        url.set("https://github.com/tillhub/input-engine")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        // Specify developers information
        developers {
            developer {
                id.set("djordjeh")
                name.set("Đorđe Hrnjez")
                email.set("dorde.hrnjez@unzer.com")
            }
            developer {
                id.set("SloInfinity")
                name.set("Martin Sirok")
                email.set("m.sirok.ext@unzer.com")
            }
            developer {
                id.set("shekar-allam")
                name.set("Chandrashekar Allam")
                email.set("chandrashekar.allam@unzer.com")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/tillhub/input-engine")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral()

    // Enable GPG signing for all publications
    signAllPublications()
}
