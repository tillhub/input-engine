enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // @Incubating API, safe in Gradle 8.1+
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tillhub_Input_Engine"
include(":sample")
include(":input-engine")
