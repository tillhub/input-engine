import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://jitpack.io")
        }
    }
}


rootProject.name = "Tillhub Input Engine"
include(":input-engine",":sample")
