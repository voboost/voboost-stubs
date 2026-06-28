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

The project is a multi-module Android Gradle build providing five APK stub modules (`launcher`, `bluetoothphone`, `systemservice`, `qgime`, `vehiclesetting`). Each module builds an installable APK whose `applicationId` matches the real vehicle process name and runs a foreground service to keep the process alive as a Frida injection target. The legacy host-side JVM stub code has been removed; the migration is complete.

## Migration History

See `openspec/changes/android-apk-port/` for the completed migration from JVM stubs to Android APK modules.
