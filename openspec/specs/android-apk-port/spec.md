# android-apk-port Specification

## Purpose
TBD - created by archiving change android-apk-port. Update Purpose after archive.
## Requirements
### Requirement: Five Android APK modules, one per stub process
The voboost-stubs project SHALL provide exactly five Android APK modules, each corresponding to a target stub process: launcher, bluetoothphone, systemservice, qgime, and vehiclesetting. Each module SHALL be a separate Gradle project using the `com.android.application` plugin and SHALL produce an installable Android APK file.

#### Scenario: All five stub modules are present
- **WHEN** the voboost-stubs project is configured
- **THEN** exactly five Android application modules exist: launcher, bluetoothphone, systemservice, qgime, vehiclesetting

### Requirement: Each module uses com.android.application plugin
Each of the five stub modules SHALL use the `com.android.application` Gradle plugin and SHALL be configured as a standalone Android application project. The module SHALL NOT be a library or Java-only project.

#### Scenario: Module uses Android application plugin
- **WHEN** any stub module's build.gradle.kts is examined
- **THEN** the module applies the `com.android.application` plugin and produces an APK output

### Requirement: Target Android 9 (API 28) with arm64-v8a only
Each Android APK module SHALL target Android 9 (API 28) as the minimum SDK version (minSdk 28) and SHALL build exclusively for the arm64-v8a architecture. The module SHALL NOT include multi-architecture support or target older API levels.

#### Scenario: Android 9 baseline with arm64-v8a only
- **WHEN** a stub module's Android configuration is examined
- **THEN** minSdk is 28, and the module builds only for arm64-v8a architecture with no other ABIs included

### Requirement: Foreground service to keep process alive
Each Android APK module SHALL run a foreground service that keeps the application process alive for Frida injection. The foreground service SHALL display a persistent notification to prevent Android from terminating the process. The service SHALL start automatically when the application launches.

#### Scenario: Foreground service keeps process alive
- **WHEN** a stub APK is installed and launched on an Android device
- **THEN** the application runs a foreground service with a persistent notification and remains alive for injection targeting

### Requirement: applicationId matches target process name
Each Android APK module SHALL use an applicationId that exactly matches the real vehicle process name it simulates. The applicationId SHALL enable Frida to target the correct process name matching production injection configuration.

#### Scenario: applicationId matches target process
- **WHEN** a stub module's manifest is examined
- **THEN** the applicationId matches the expected target process name (e.g., com.android.launcher3, com.android.bluetoothphone)

### Requirement: Legacy mock Android classes removed
The Android APK modules SHALL NOT contain legacy mock Android classes from the JVM stub implementation. All Android framework dependencies SHALL use real Android SDK classes rather than mock implementations. Mock classes SHALL be removed from the codebase.

#### Scenario: No legacy mock Android classes
- **WHEN** the Android APK module source code is examined
- **THEN** no mock Android classes exist and all Android dependencies use the real Android SDK

### Requirement: Host-side tests NOT ported
The existing host-side JVM tests SHALL NOT be ported to the Android APK modules. Device-side Android tests MAY be added for basic functionality verification, but the primary testing approach is Frida injection testing rather than unit tests.

#### Scenario: Host-side tests not ported
- **WHEN** the Android APK modules are implemented
- **THEN** previous host-side JVM tests are not ported, and device testing focuses on injection validation rather than unit tests

