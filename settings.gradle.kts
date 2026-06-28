pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "voboost-stubs"

include(":launcher")
include(":bluetoothphone")
include(":systemservice")
include(":qgime")
include(":vehiclesetting")
