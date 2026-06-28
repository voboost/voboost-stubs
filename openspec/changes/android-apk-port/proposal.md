## Why

Voboost requires realistic Android process targets for Frida injection testing during development. Currently, voboost-stubs provides 5 JVM main() classes that mock vehicle infotainment processes, but these don't exercise the real Android application lifecycle, permissions model, or process isolation boundaries that Frida encounters in production.

Testing against host-side JVM stubs misses critical Android-specific behaviors:
- Process startup and foreground service lifecycle
- Android permission system and security boundaries  
- APK signing and installation process
- Real Android process names and applicationId matching
- Multi-process Android application architecture

Porting these stubs to Android APKs enables realistic device testing and validates that Frida injection works correctly against actual Android application targets rather than JVM simulations.

## What Changes

Convert the voboost-stubs project from a pure JVM Gradle build to a multi-module Android application project. Each stub process becomes a separate Android APK module:

- **launcher** - Android launcher application stub
- **bluetoothphone** - Bluetooth telephony service stub  
- **systemservice** - Android system service stub
- **qgime** - Input method editor stub
- **vehiclesetting** - Vehicle settings management stub

Each APK module will:
- Use the `com.android.application` plugin
- Target Android 9 (API 28) with arm64-v8a architecture only
- Run a foreground service to keep the process alive for Frida injection
- Use applicationId matching the target process name for injection accuracy
- Remove legacy mock Android classes in favor of real Android framework

## Capabilities

### New Capabilities
- `android-apk-port`: Complete conversion of 5 JVM stubs to Android APK modules with foreground services

### Modified Capabilities  
None. This is a foundational capability addition for voboost-stubs.

## Impact

- **voboost-stubs**: Transformed from single JVM project to multi-module Android build
- **Development workflow**: Android APK deployment via `adb install` instead of JVM execution
- **Testing**: Realistic device testing environment matching production Frida injection targets
- **Build system**: Multi-module Gradle project with Android application plugins
- **Dependencies**: Android SDK, build tools, and platform dependencies instead of JVM-only libraries
