# voboost-stubs

Stub applications for local testing of Frida agents without Android emulator.

## Overview

This repository provides Kotlin stub applications that simulate Android processes for testing Frida agents locally on macOS.

- Stubs provide process names that match Android packages
- Stubs simulate Java/Kotlin classes that agents hook into
- Agents from voboost-script are injected into stub processes for testing

## Prerequisites

1. Install Frida tools:
```bash
pip3 install frida-tools
```

2. Install Kotlin compiler:
```bash
brew install kotlin
```

3. Install Java (JDK 11+):
```bash
brew install openjdk@11
```

## Quick Start

### 1. Build Frida scripts

```bash
cd ../voboost-script
npm install
npm run build
```

### 2. Compile Kotlin stubs

```bash
cd apps
kotlinc com/qinggan/app/launcher/LauncherStub.kt -include-runtime -d LauncherStub.jar
kotlinc com/qinggan/bluetoothphone/BluetoothPhoneStub.kt -include-runtime -d BluetoothPhoneStub.jar
kotlinc com/qinggan/systemservice/SystemServiceStub.kt -include-runtime -d SystemServiceStub.jar
kotlinc com/qinggan/app/qgime/QgimeStub.kt -include-runtime -d QgimeStub.jar
kotlinc com/qinggan/app/vehiclesetting/VehicleSettingStub.kt -include-runtime -d VehicleSettingStub.jar
kotlinc okhttp3/*.kt -d okhttp3.jar
```

### 3. Start stub process

```bash
cd apps
java -jar LauncherStub.jar
```

### 4. Run voboost desktop

In another terminal:

```bash
cd ../voboost
./gradlew runDesktop
```

## Available Stubs

| Stub | Process Name | Agents |
|------|--------------|--------|
| LauncherStub | com.qinggan.app.launcher | weather-widget-mod, app-launcher-mod, navbar-launcher-mod, app-viewport-mod |
| BluetoothPhoneStub | com.qinggan.bluetoothphone | phone-num-mod |
| SystemServiceStub | com.qinggan.systemservice | app-multi-display, voboost-to-menu-mod, forced-ev-mod |
| QgimeStub | com.qinggan.app.qgime | keyboard-ru-mod, keyboard-lock-en-mod |
| VehicleSettingStub | com.qinggan.app.vehiclesetting | ADAS-activation-mod |

## Testing

### Optimized Testing Configuration

The project uses a consolidated AVA configuration that supports both parallel and serial execution modes:

- **Parallel mode:** 100 concurrent workers for maximum performance
- **Serial mode:** Single worker for maximum stability and debugging
- **Dynamic configuration:** Automatically selects mode based on environment variables
- **Worker isolation:** Full process isolation in parallel mode

### Run All Tests

```bash
npm test                    # Runs tests in parallel mode (default)
npm run test:serial         # Runs tests in serial mode
npm run test:verbose        # Verbose output in parallel mode
```

### Run Specific Test Files

```bash
npm run test:launcher        # Test launcher stub
npm run test:bluetoothphone  # Test Bluetooth phone stub
npm run test:systemservice   # Test system service stub
npm run test:qgime          # Test QGIME stub
npm run test:vehiclesetting  # Test vehicle setting stub
```

### Test Execution Modes

The consolidated configuration supports both parallel and serial execution:

**Parallel Mode (Default):**
- 100 concurrent workers for maximum performance
- Worker isolation with unique process IDs
- Optimized for CI/CD pipelines and multi-core systems
- 60 second timeout per test

**Serial Mode:**
- Single worker execution for maximum stability
- Easier debugging and tracing
- Compatible with tests requiring specific execution order
- 5 minute timeout per test

Use `npm run test:serial` for debugging or when tests have dependencies.

### Test Architecture

The test suite uses a modern architecture with proper process isolation:

- **ProcessManager** ([`lib/test-process-manager.js`](lib/test-process-manager.js)) - Manages process lifecycle with locking to prevent race conditions
- **TestUtils** ([`lib/test-utils.js`](lib/test-utils.js)) - High-level test utilities for common patterns
- **TestFixtures** ([`lib/test-fixtures.js`](lib/test-fixtures.js)) - Centralized test configurations
- **FridaTestHelper** ([`lib/test-frida-helper.js`](lib/test-frida-helper.js)) - Low-level Frida operations

### Writing Tests

```javascript
import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

// Global cleanup after all tests
test.after.always(async () => {
    await TestUtils.cleanupAll();
});

test.serial('my test', async t => {
    const result = await TestUtils.runStandardAgentTest(
        t,
        'agent-name',
        'process-name',
        { params: { /* custom params */ } }
    );
    t.true(result.success);
});
```

### Test Features

- ✅ **Process Isolation** - Tests use locking to prevent race conditions
- ✅ **Process Reuse** - Processes are reused across tests for performance
- ✅ **Automatic Cleanup** - ProcessManager handles cleanup automatically
- ✅ **Simplified API** - TestUtils provides high-level test methods
- ✅ **Centralized Config** - Test configurations in one place

See [`plans/REFACTORING-SUMMARY.md`](plans/REFACTORING-SUMMARY.md) for details on the test architecture improvements.

## Manual Testing

```bash
# List running processes
frida-ps

# Inject agent manually (use process name, not 'java')
frida -n com.qinggan.app.launcher -l ../voboost-script/build/weather-widget-mod_3debug.js
```

## Migration from Java to Kotlin

All stub applications have been converted from Java to Kotlin for better code quality and consistency with the main voboost project. The conversion includes:

- Reduced boilerplate code using Kotlin idioms
- Improved null safety
- Better resource management with `use` blocks
- Cleaner singleton patterns with `object` declarations
- More concise syntax while maintaining full JVM compatibility

The compiled Kotlin code runs identically to the previous Java version and is fully compatible with Frida injection.

## Code Style

This project follows the unified Voboost code style from [voboost-codestyle](../voboost-codestyle).

### Linting and Formatting

```bash
npm run lint      # Fix all JS and Kotlin files
npm run lint:js   # Fix JS files only (ESLint + Prettier)
npm run lint:kt   # Fix Kotlin files only (ktlint)
```

### Rules
- **JavaScript**: 100 char line, 4 spaces, single quotes, no console except Logger
- **Kotlin**: ktlint defaults + .editorconfig settings

See [voboost-codestyle README](../voboost-codestyle/README.md) for full documentation.

## Project Structure

```
voboost-stubs/
├── apps/                           # Kotlin stub applications
│   ├── com/qinggan/                # Package structure
│   └── *.jar                       # Compiled JAR files
├── lib/                            # Test infrastructure
│   ├── frida-test-helper.js        # Low-level Frida operations
│   ├── test-utils.js               # High-level test utilities
│   ├── test-process-manager.js     # Process lifecycle management
│   └── test-fixtures.js            # Test configurations
├── test/                           # Test files
│   ├── bluetoothphone.test.js      # Bluetooth phone stub tests
│   ├── launcher.test.js            # Launcher stub tests
│   ├── qgime.test.js               # QGIME stub tests
│   ├── systemservice.test.js       # System service stub tests
│   └── vehiclesetting.test.js      # Vehicle setting stub tests
├── config/                         # Configuration files
└── plans/                          # Planning documents
```

## License

GPL-3.0
