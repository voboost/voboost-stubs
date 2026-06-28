buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Apply Voboost checkstyle configuration for Java projects (but don't run on build)
apply(from = "../voboost-codestyle/checkstyle.gradle")

subprojects {
    // Apply checkstyle to subprojects only if they have the checkstyle task
    afterEvaluate {
        if (tasks.names.contains("checkJavaStyle")) {
            tasks.named("checkJavaStyle") {
                enabled = true
                notCompatibleWithConfigurationCache("Checkstyle task has issues with configuration cache")
            }
        }
    }
}

// Fix Guava dependency conflict for checkstyle
configurations.all {
    exclude(group = "com.google.collections", module = "google-collections")
    resolutionStrategy {
        force("com.google.guava:guava:32.1.2-jre")
    }
}

group = "ru.voboost.stubs"
version = "1.0.0"

// Task to build all stub APKs
tasks.register("buildAllStubs") {
    group = "build"
    description = "Build all stub APK files"
    dependsOn(
        ":launcher:assemble",
        ":bluetoothphone:assemble",
        ":systemservice:assemble",
        ":qgime:assemble",
        ":vehiclesetting:assemble"
    )
}
