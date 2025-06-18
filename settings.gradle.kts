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

rootProject.name = "input-engine"
include(":sample")
include(":input-engine")
include(":input-engine:formatter")
include(":input-engine:financial")

