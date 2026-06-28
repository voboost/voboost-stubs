# Voboost Stubs

Voboost-stubs is a collection of stub Android applications that provide Frida injection targets for the Voboost system. These stubs replicate the process names and basic structure of real vehicle infotainment system applications.

## Purpose

These stub applications serve as minimal Android APK targets for Frida injection during development and testing. Each stub corresponds to a real process in the target vehicle system:

- `launcher` - Android launcher process
- `bluetoothphone` - Bluetooth telephony service  
- `systemservice` - Android system service
- `qgime` - Input method editor
- `vehiclesetting` - Vehicle settings management

## Current State

The project is currently a pure JVM Gradle project with host-side main() classes that mock the target process behavior. This architecture is being migrated to Android APK modules for realistic device testing.

## Migration

See `openspec/changes/android-apk-port/` for the ongoing migration from JVM stubs to Android APK modules.
