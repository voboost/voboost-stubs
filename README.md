# voboost-stubs

Android stub APK applications that serve as realistic Frida injection targets
for the Voboost system during development and testing.

## Overview

Voboost-stubs provides a set of minimal Android APK modules that replicate the
process names and basic structure of the real vehicle infotainment system
applications. Each stub runs as a real Android process (with a foreground
service keeping it alive) so that Frida agents from
[`voboost-script`](../voboost-script) can be injected against the same process
names, lifecycle, and security boundaries that exist in production.

- Each stub is a separate `com.android.application` Gradle module
- Each APK's `applicationId` matches the real vehicle process name
- Each APK runs a foreground service so the process stays alive for injection
- Targets Android 9 (API 28) on `arm64-v8a` only (matching the vehicle hardware)

This repository is part of the Voboost platform — see
[Relationship to other repos](#relationship-to-other-repos) below.

## Architecture

The project provides multi-module Android APK stub targets for Frida injection
testing. Each stub lives under its own top-level directory (`launcher/`,
`bluetoothphone/`, `systemservice/`, `qgime/`, `vehiclesetting/`) and builds an
installable APK whose `applicationId` matches the real vehicle process name.

The design and rationale for this architecture are documented as an OpenSpec
change in [`openspec/changes/android-apk-port/`](openspec/changes/android-apk-port/).

## Prerequisites

### Common

- **JDK 17** (required by the Android Gradle Plugin)
- **Android SDK** with platform 35 and build-tools (set `ANDROID_HOME`)
- **Node.js** (for the JS-based test suite and linting)

### macOS

```bash
brew install openjdk@17 node
# Android SDK via Android Studio or sdkmanager
pip3 install frida-tools   # for manual injection
```

### Windows

- JDK 17 from [Adoptium](https://adoptium.net/) (set `JAVA_HOME`)
- Android Studio for the SDK
- Node.js from [nodejs.org](https://nodejs.org/)
- `pip install frida-tools` for manual injection

Install JS dependencies once:

```bash
npm install
```

## Available stubs

| Module | applicationId / process name | Purpose |
|--------|------------------------------|---------|
| `launcher` | `com.qinggan.app.launcher` | Launcher process |
| `bluetoothphone` | `com.qinggan.bluetoothphone` | Bluetooth telephony service |
| `systemservice` | `com.qinggan.systemservice` | System service |
| `qgime` | `com.qinggan.app.qgime` | Input method editor |
| `vehiclesetting` | `com.qinggan.app.vehiclesetting` | Vehicle settings |

Each module produces a release APK named `<module>.apk` (e.g. `launcher.apk`)
in `<module>/build/outputs/apk/release/`.

## Build

Build all stub APKs:

```bash
./gradlew buildAllStubs
```

Build a single stub:

```bash
./gradlew :launcher:assemble
```

On Windows use `.\gradlew.bat` instead of `./gradlew`.

The release variant is debuggable when `-Pdebuggable=true` is passed; by default
the debug variant is disabled and only release is built.

## Deploy to a device

```bash
adb install -r launcher/build/outputs/apk/release/launcher.apk
adb shell am start -n com.qinggan.app.launcher/.LauncherActivity
```

The foreground service keeps the process alive so Frida can attach by process
name.

## Testing

The JS test suite (AVA) injects agents from `voboost-script` into the stub
processes and validates behaviour. It uses PID-based injection for full
parallelization.

```bash
npm test                 # run all tests
npm run test:launcher    # launcher stub
npm run test:phone       # bluetoothphone stub
npm run test:system      # systemservice stub
npm run test:keyboard    # qgime stub
npm run test:vehicle     # vehiclesetting stub
```

Test infrastructure lives under [`lib/`](lib/) (`Utils.js`, `Frida.js`,
`Fixtures.js`, `MultiAgent.js`, `ProcessHealth.js`, `Retry.js`,
`ErrorHandler.js`, `Debug.js`, `Injection.js`).

### Manual injection

`frida-inject` is required for passing parameters to agents:

```bash
frida-ps
frida-inject -n com.qinggan.app.launcher \
  -s ../voboost-script/build/weather-widget-mod.js \
  --parameters='{"key":"value"}'
```

## Project structure

```
voboost-stubs/
├── launcher/            # Android APK stub module (com.qinggan.app.launcher)
├── bluetoothphone/      # Android APK stub module (com.qinggan.bluetoothphone)
├── systemservice/       # Android APK stub module (com.qinggan.systemservice)
├── qgime/               # Android APK stub module (com.qinggan.app.qgime)
├── vehiclesetting/      # Android APK stub module (com.qinggan.app.vehiclesetting)
├── lib/                 # JS test infrastructure (modular)
├── test/                # AVA test files (parallel)
├── config/              # AVA + ESLint configuration
├── openspec/            # Spec-driven changes (android-apk-port)
├── build.gradle.kts     # Root multi-module Android build
├── settings.gradle.kts  # Includes the 5 APK modules
└── gradle.properties    # AndroidX + Gradle settings
```

## Relationship to other repos

Voboost is a multi-repo platform:

- [`voboost`](../voboost) — the Android app (UI, OTA client, device management).
- [`voboost-inject`](../voboost-inject) — the root daemon (Vala) that injects
  Frida agents into vehicle processes and self-updates via APK-level OTA.
- [`voboost-script`](../voboost-script) — the Frida agent JS modules injected
  into the target processes.
- **`voboost-stubs`** (this repo) — Android APK stubs that stand in for the
  real vehicle processes during development and testing.
- [`voboost-install`](../voboost-install) — device installation tooling.

The stubs let `voboost-inject` and `voboost-script` be exercised against real
Android process names and lifecycles without the production vehicle firmware.

## Code style

This project follows the unified Voboost code style from
[`voboost-codestyle`](../voboost-codestyle). For AI agent rules and coding
guidelines, see [AGENTS.md](AGENTS.md).

```bash
npm run lint        # fix all JS and Java files
npm run lint:js     # JS only (ESLint + Prettier)
npm run lint:java   # Java only (checkstyle)
```

Rules:

- **JavaScript**: 100 char line, 4 spaces, single quotes, console only in DEBUG
- **Java**: Checkstyle configuration from voboost-codestyle

## Troubleshooting

### Android build issues

Ensure `ANDROID_HOME` points at an SDK with platform 35 and build-tools
installed. The build targets `arm64-v8a` only; an x86 emulator will not install
the output APKs — use an arm64 image.

### Orphaned stub processes

```bash
adb shell ps | grep qinggan
adb shell am force-stop com.qinggan.app.launcher
```

### Debug logging

```bash
DEBUG=1 npm test
```


## License

Dual-licensed:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE) — free for personal use
- [Commercial license](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.md) — required otherwise
