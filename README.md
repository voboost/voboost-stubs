# voboost-stubs

Stub applications for local testing of Frida agents without Android emulator.

## Overview

This repository provides Java stub applications that simulate Android processes for testing Frida agents locally on macOS, Linux, and Windows.

- Stubs provide process names that match Android packages
- Stubs simulate Java classes that agents hook into
- Agents from voboost-script are injected into stub processes for testing
- Gradle-based build system for easy compilation and management

## Prerequisites

### macOS

1. Install Java (JDK 11+):
```bash
brew install openjdk@11
```

2. Install Frida tools:
```bash
pip3 install frida-tools
```

3. Install frida-inject (required for parameter passing):
```bash
# Download from GitHub releases
# https://github.com/frida/frida/releases

# For macOS: download frida-inject-*-macos-universal.tar.xz
# Extract and add to PATH, e.g.:
sudo cp frida-inject-*-macos-universal/bin/frida-inject /usr/local/bin/
```

4. Install Node.js dependencies:
```bash
npm install
```

### Windows

1. Install Java (JDK 11+):
   - Download from [Adoptium](https://adoptium.net/) or use Chocolatey:
   ```powershell
   choco install temurin11
   ```
   - Add JAVA_HOME to environment variables

2. Install Python and Frida tools:
   - Download Python from [python.org](https://www.python.org/downloads/)
   - Install Frida:
   ```powershell
   pip install frida-tools
   ```

3. Install frida-inject (required for parameter passing):
   - Download from GitHub releases: https://github.com/frida/frida/releases
   - For Windows: download frida-inject-*-windows-x86_64.exe or frida-inject-*-windows-x86.exe
   - Extract and add to PATH, or place in a directory in your PATH

4. Install Node.js:
   - Download from [nodejs.org](https://nodejs.org/) or use Chocolatey:
   ```powershell
   choco install nodejs
   ```

5. Install dependencies:
   ```powershell
   npm install
   ```

6. Use `gradlew.bat` instead of `./gradlew`:
   ```powershell
   .\gradlew.bat build
   ```

## Quick Start

### 1. Build Frida scripts

**macOS/Linux:**
```bash
cd ../voboost-script
npm install
npm run build
```

**Windows:**
```powershell
cd ..\voboost-script
npm install
npm run build
```

### 2. Build Java stubs

**macOS/Linux:**
```bash
cd voboost-stubs
./gradlew build
```

**Windows:**
```powershell
cd voboost-stubs
.\gradlew.bat build
```

This compiles all stub applications and creates executable JAR files in the `build/` directory.

### 3. Start stub process

**macOS/Linux:**
```bash
# Start individual stub
npm run start:launcher

# Start CanBus-related stubs (systemservice + vehiclesetting)
npm run start:canbus

# Or start all stubs concurrently
npm start
```

**Windows:**
```powershell
# Start individual stub
npm run start:launcher

# Start CanBus-related stubs (systemservice + vehiclesetting)
npm run start:canbus

# Or start all stubs concurrently
npm start
```

### 4. Run voboost desktop

In another terminal:

**macOS/Linux:**
```bash
cd ../voboost
./gradlew runDesktop
```

**Windows:**
```powershell
cd ..\voboost
.\gradlew.bat runDesktop
```

## Available Stubs

| Stub | Process Name | Agents |
|------|--------------|--------|
| LauncherStub | com.qinggan.app.launcher | weather-widget-mod, app-launcher-mod, navbar-launcher-mod, app-viewport-mod, media-source-mod, media-window-mod |
| BluetoothPhoneStub | com.qinggan.bluetoothphone | phone-num-mod |
| SystemServiceStub | com.qinggan.systemservice | app-multi-display, voboost-to-menu-mod, forced-ev-mod |
| QgimeStub | com.qinggan.app.qgime | keyboard-ru-mod, keyboard-lock-en-mod |
| VehicleSettingStub | com.qinggan.app.vehiclesetting | ADAS-activation-mod, low-speed-sound-mod |

## CanBus Testing

The `canbus.test.js` file tests agents that use `CanBusManager` across multiple processes:

- **forced-ev-mod** - Injects into `systemservice` process
- **low-speed-sound-mod** - Injects into `vehiclesetting` process

To run CanBus tests:
```bash
npm run test:canbus
```

To start the required stubs for CanBus testing:
```bash
npm run start:canbus  # Starts both systemservice and vehiclesetting stubs
```

## Testing

### Optimized Testing Configuration

The project uses an AVA configuration that supports parallel execution with an option for serial debugging:

- **Parallel mode:** 100 concurrent workers for maximum performance
- **Serial debugging:** Single worker for stability and debugging (via environment variable)
- **Dynamic configuration:** Automatically selects mode based on environment variables
- **Worker isolation:** Full process isolation in parallel mode

### Run All Tests

```bash
npm test                    # Runs tests in parallel mode (default)
npm run test:verbose        # Verbose output in parallel mode
AVA_PARALLEL=false npm test # Runs tests in serial mode for debugging
```

### Run Specific Test Files

```bash
npm run test:launcher        # Test launcher stub
npm run test:phone          # Test Bluetooth phone stub
npm run test:system         # Test system service stub
npm run test:qgime          # Test QGIME stub
npm run test:vehicle        # Test vehicle setting stub
npm run test:canbus         # Test CanBus functionality (uses system & vehicle stubs)
```

### Test Execution Modes

The consolidated configuration supports parallel execution with serial debugging option:

**Parallel Mode (Default):**
- 100 concurrent workers for maximum performance
- Worker isolation with unique process IDs
- Optimized for CI/CD pipelines and multi-core systems
- 60 second timeout per test

**Serial Debugging Mode:**
- Single worker execution for maximum stability
- Easier debugging and tracing
- Use `AVA_PARALLEL=false npm test` when tests have dependencies or need debugging

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

test('my test', async t => {
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

- **Process Isolation** - Tests use locking to prevent race conditions
- **Process Reuse** - Processes are reused across tests for performance
- **Automatic Cleanup** - ProcessManager handles cleanup automatically
- **Simplified API** - TestUtils provides high-level test methods
- **Centralized Config** - Test configurations in one place

## Manual Testing

**Note**: `frida-inject` is required for passing parameters to agents. Make sure it's installed as described in the Prerequisites section.

**macOS/Linux:**
```bash
# List running processes
frida-ps

# Inject agent manually with parameters (use process name, not 'java')
frida-inject -n com.qinggan.app.launcher -l ../voboost-script/build/weather-widget-mod_3debug.js --parameters='{"key":"value"}'
```

**Windows:**
```powershell
# List running processes
frida-ps

# Inject agent manually with parameters (use process name, not 'java')
frida-inject -n com.qinggan.app.launcher -l ..\voboost-script\build\weather-widget-mod_3debug.js --parameters='{\"key\":\"value\"}'
```

## Code Style

This project follows the unified Voboost code style from [voboost-codestyle](../voboost-codestyle).

### Linting and Formatting

```bash
npm run lint      # Fix all JS and Java files
npm run lint:js   # Fix JS files only (ESLint + Prettier)
npm run lint:java # Fix Java files only (checkstyle)
```

### Rules
- **JavaScript**: 100 char line, 4 spaces, single quotes, no console except Logger
- **Java**: Checkstyle configuration from voboost-codestyle

See [voboost-codestyle README](../voboost-codestyle/README.md) for full documentation.

## Project Structure

```
voboost-stubs/
├── apps/                           # Java stub applications
│   ├── android/                    # Android framework stubs
│   ├── com/qinggan/                # Main package structure
│   ├── com/pateo/                  # Pateo-specific stubs
│   └── okhttp3/                    # OkHttp stubs
├── build/                          # Build output directory
│   ├── *.jar                       # Compiled JAR files
│   └── classes/                    # Compiled classes
├── lib/                            # Test infrastructure
│   ├── test-frida-helper.js        # Low-level Frida operations
│   ├── test-utils.js               # High-level test utilities
│   ├── test-process-manager.js     # Process lifecycle management
│   └── test-fixtures.js            # Test configurations
├── test/                           # Test files
│   ├── bluetoothphone.test.js      # Bluetooth phone stub tests
│   ├── canbus.test.js              # CanBus stub tests
│   ├── launcher.test.js            # Launcher stub tests
│   ├── qgime.test.js               # QGIME stub tests
│   ├── systemservice.test.js       # System service stub tests
│   ├── vehiclesetting.test.js      # Vehicle setting stub tests
│   └── fixtures/                   # Test fixtures directory
├── config/                         # Configuration files
│   ├── config-ava.mjs              # AVA test configuration
│   └── config-eslint.mjs           # ESLint configuration
└── gradle/                         # Gradle wrapper
```

## Gradle Tasks

### Build Tasks
**macOS/Linux:**
```bash
./gradlew build                    # Build all stubs
./gradlew buildAllStubJars         # Build all JAR files
./gradlew LauncherStubJar          # Build specific stub JAR
./gradlew clean                    # Clean build directory
```

**Windows:**
```powershell
.\gradlew.bat build                # Build all stubs
.\gradlew.bat buildAllStubJars     # Build all JAR files
.\gradlew.bat LauncherStubJar      # Build specific stub JAR
.\gradlew.bat clean                # Clean build directory
```

### Run Tasks
**macOS/Linux:**
```bash
./gradlew runLauncherStub          # Run launcher stub
./gradlew runBluetoothPhoneStub    # Run Bluetooth phone stub
./gradlew runSystemServiceStub     # Run system service stub
./gradlew runQgimeStub             # Run QGIME stub
./gradlew runVehicleSettingStub    # Run vehicle setting stub
```

**Windows:**
```powershell
.\gradlew.bat runLauncherStub      # Run launcher stub
.\gradlew.bat runBluetoothPhoneStub # Run Bluetooth phone stub
.\gradlew.bat runSystemServiceStub  # Run system service stub
.\gradlew.bat runQgimeStub          # Run QGIME stub
.\gradlew.bat runVehicleSettingStub # Run vehicle setting stub
```

### Style Check Tasks
**macOS/Linux:**
```bash
./gradlew checkJavaStyle           # Run checkstyle on Java files
```

**Windows:**
```powershell
.\gradlew.bat checkJavaStyle       # Run checkstyle on Java files
```


## License

Dual-licensed:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE) — free for personal use
- [Commercial license](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.md) — required otherwise
