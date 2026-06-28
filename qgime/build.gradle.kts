plugins {
    id("com.android.application")
}

android {
    namespace = "com.qinggan.app.qgime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.qinggan.app.qgime"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Single release build; debug variant disabled below.
            isDebuggable = (project.findProperty("debuggable")?.toString() == "true")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Strip build-type suffix: qgime.apk, not qgime-release.apk
    applicationVariants.all {
        outputs.forEach { output ->
            val apk = output as com.android.build.gradle.api.ApkVariantOutput
            apk.outputFileName = apk.outputFileName.replace("-release", "")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }
}

dependencies {
    implementation("androidx.core:core:1.15.0")
}

// Release-only: drop the debug variant entirely.
androidComponents {
    beforeVariants { variant ->
        variant.enable = variant.buildType != "debug"
    }
}
