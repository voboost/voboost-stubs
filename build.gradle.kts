plugins {
    id("application")
}

// Apply Voboost checkstyle configuration for Java projects (but don't run on build)
apply(from = "../voboost-codestyle/checkstyle.gradle")

// Disable checkstyle for regular build to avoid issues with missing files
tasks.withType<Checkstyle>().configureEach {
    enabled = false
}

// Only enable specific checkstyle tasks when explicitly called
tasks.named("checkJavaStyle") {
    enabled = true
    // Disable configuration cache for this task
    notCompatibleWithConfigurationCache("Checkstyle task has issues with configuration cache")
    // Override source directory to point to apps instead of src/main/java
    doFirst {
        // Configure checkstyle to look in the right directory
        (this as Checkstyle).setSource(fileTree("apps") {
            include("**/*.java")
            exclude("**/generated/**")
        })
    }
}

// Fix Guava dependency conflict for checkstyle
configurations.all {
    exclude(group = "com.google.collections", module = "google-collections")
    resolutionStrategy {
        force("com.google.guava:guava:32.1.2-jre")
    }
}

// Disable all tasks that might create bin directory
tasks.matching { task -> task.name.contains("dist") || task.name.contains("installDist") || task.name.contains("startScripts") }.configureEach {
    enabled = false
}

// Specifically disable the tasks that create bin directory
tasks.named("installDist") {
    enabled = false
}
tasks.named("distTar") {
    enabled = false
}
tasks.named("distZip") {
    enabled = false
}
tasks.named("assembleDist") {
    enabled = false
}
tasks.withType<CreateStartScripts>().configureEach {
    enabled = false
}

// Enable Java compilation
tasks.withType<JavaCompile>().configureEach {
    enabled = true
}

group = "ru.voboost.stubs"
version = "1.0.0"

sourceSets {
    main {
        java {
            srcDir("apps")
        }
        resources {
            setSrcDirs(emptyList<String>()) // No resources
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

// Configure jar task to handle duplicates
tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Tasks for running individual stubs
tasks.register<JavaExec>("runLauncherStub") {
    group = "application"
    description = "Run LauncherStub"
    mainClass.set("com.qinggan.app.launcher.LauncherStub")
    classpath = sourceSets.named("main").get().runtimeClasspath
}

tasks.register<JavaExec>("runBluetoothPhoneStub") {
    group = "application"
    description = "Run BluetoothPhoneStub"
    mainClass.set("com.qinggan.bluetoothphone.BluetoothPhoneStub")
    classpath = sourceSets.named("main").get().runtimeClasspath
}

tasks.register<JavaExec>("runSystemServiceStub") {
    group = "application"
    description = "Run SystemServiceStub"
    mainClass.set("com.qinggan.systemservice.SystemServiceStub")
    classpath = sourceSets.named("main").get().runtimeClasspath
}

tasks.register<JavaExec>("runKeyboardStub") {
    group = "application"
    description = "Run KeyboardStub"
    mainClass.set("com.qinggan.app.qgime.QgimeStub")
    classpath = sourceSets.named("main").get().runtimeClasspath
}

tasks.register<JavaExec>("runVehicleSettingStub") {
    group = "application"
    description = "Run VehicleSettingStub"
    mainClass.set("com.qinggan.app.vehiclesetting.VehicleSettingStub")
    classpath = sourceSets.named("main").get().runtimeClasspath
}

// Helper function to create executable JAR tasks
fun createStubJarTask(
    stubName: String,
    mainClassName: String
) {
    tasks.register<Jar>("${stubName}Jar") {
        group = "build"
        description = "Create executable JAR for $stubName"
        archiveBaseName.set(stubName)
        archiveVersion.set("")
        destinationDirectory.set(file("build"))

        // Include all compiled classes
        from(sourceSets.main.get().output)

        // Also include all compiled classes from build/classes
        from("build/classes/java/main")

        manifest {
            attributes["Main-Class"] = mainClassName
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

// Create JAR tasks for all stubs
createStubJarTask("LauncherStub", "com.qinggan.app.launcher.LauncherStub")
createStubJarTask("BluetoothPhoneStub", "com.qinggan.bluetoothphone.BluetoothPhoneStub")
createStubJarTask("SystemServiceStub", "com.qinggan.systemservice.SystemServiceStub")
createStubJarTask("QgimeStub", "com.qinggan.app.qgime.QgimeStub")
createStubJarTask("VehicleSettingStub", "com.qinggan.app.vehiclesetting.VehicleSettingStub")

// Task to build all stub JARs
tasks.register("buildAllStubJars") {
    group = "build"
    description = "Build all stub JAR files"
    dependsOn(
        "LauncherStubJar",
        "BluetoothPhoneStubJar",
        "SystemServiceStubJar",
        "QgimeStubJar",
        "VehicleSettingStubJar"
    )
}

// Make build task depend on building all stub JARs
tasks.named("build") {
    dependsOn("buildAllStubJars")
}
