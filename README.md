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

**Note**: The `frida-java-bridge` package is included as a devDependency for type definitions and IDE support. It is not used at runtime - actual Frida injection uses the system-installed `frida-inject` tool.

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

### 3. Run tests

```bash
npm test
```

## Available Stubs

| Stub | Process Name | Agents |
|------|--------------|--------|
| LauncherStub | com.qinggan.app.launcher | weather-widget-mod, app-launcher-mod, navbar-launcher-mod, app-viewport-mod, media-source-mod, media-window-mod |
| BluetoothPhoneStub | com.qinggan.bluetoothphone | phone-num-mod |
| SystemServiceStub | com.qinggan.systemservice | app-multi-display-mod, voboost-to-menu-mod, forced-ev-mod |
| QgimeStub | com.qinggan.app.qgime | keyboard-ru-mod, keyboard-lock-en-mod |
| VehicleSettingStub | com.qinggan.app.vehiclesetting | adas-activation-mod, low-speed-sound-mod |

## Testing

### Test Architecture

The test suite uses PID-based injection for full parallelization:

- **TestUtils** (`lib/test-utils.js`) - High-level test utilities for common patterns
- **TestFixtures** (`lib/test-fixtures.js`) - Centralized test configurations
- **FridaTestHelper** (`lib/test-frida-helper.js`) - Low-level Frida operations

All tests run in parallel with PID-based injection, where each test gets its own isolated process.

Error handling and process isolation tests are centralized in `test/error.test.js`.

### Run Tests

```bash
npm test              # Run all tests
```

### Run Specific Test Files

```bash
npm run test:launcher   # Test launcher stub
npm run test:phone      # Test Bluetooth phone stub
npm run test:system     # Test system service stub
npm run test:keyboard   # Test keyboard stub
npm run test:vehicle    # Test vehicle setting stub
```

### Test Files

| File | Description |
|------|-------------|
| launcher.test.js | Launcher agents + multi-agent injection |
| bluetoothphone.test.js | Bluetooth phone agents |
| keyboard.test.js | Keyboard agents + multi-agent injection |
| systemservice.test.js | System service agents + multi-agent injection |
| vehiclesetting.test.js | Vehicle setting agents + multi-agent injection |
| error.test.js | Shared error handling + process isolation tests |

### Writing Tests

#### Basic Injection Test

```javascript
import test from 'ava';
import { TestUtils } from '../lib/test-utils.js';

test('my-agent: default injection', async (t) => {
    await TestUtils.runBasicInjectionTest(t, 'my-agent', 'target-process');
});
```

#### Multi-Agent Injection Test

```javascript
test('process: multi-agent injection', async (t) => {
    const agents = ['agent1', 'agent2', 'agent3'];
    await TestUtils.runMultipleAgentsTest(t, 'target-process', agents);
});
```

#### Error Handling Test

```javascript
test('process: error handling', async (t) => {
    await TestUtils.runErrorHandlingTest(t, 'valid-agent', 'valid-process');
});
```

## Test Utilities

### Primary Methods

#### `runBasicInjectionTest(t, agentName, processName)`
Simplified basic injection test with built-in assertions.

**Features:**
- Handles all setup, injection, validation, and cleanup automatically
- Includes primary assertion for validation success
- Logs detailed information only on test failure
- Uses PID-based injection for full parallelization

#### `runMultipleAgentsTest(t, processName, agentNames, options)`
Injects multiple agents into the same process.

**Options:**
- `assertSuccess` (default: true) - Assert that all injections succeed
- `assertValidation` (default: true) - Assert that all validations pass
- `timeout` - Custom timeout for injections
- `successMessage` - Custom success message for assertions

**Features:**
- Injects multiple agents into the same process sequentially
- Includes built-in assertions for all injections and validations
- Uses PID-based injection for process isolation
- Automatically handles cleanup

#### `runErrorHandlingTest(t, agentName, processName)`
Standardized error handling test for invalid scenarios.

**Features:**
- Tests invalid process names and agent scripts
- Validates proper error handling and messaging
- Uses standardized error assertion patterns

### Advanced Methods

#### `runStandardAgentTest(t, agentName, processName, options)`
Full-featured agent test with extensive customization options.

**Options:**
- `params` - Custom parameters for the agent
- `timeout` - Test timeout
- `customValidation` - Custom validation function

#### `createAgentTestConfig(agentName, processName, options)`
Creates a complete agent test configuration with proper defaults.

#### `cleanup()`
Cleanup method for tests. Should be called in `test.after.always` hooks if needed.

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
├── lib/                            # Test infrastructure (modular)
│   ├── Utils.js                    # High-level test utilities + orchestration
│   ├── Frida.js                    # Core Frida operations + process management
│   ├── Fixtures.js                 # Test configurations and patterns
│   ├── MultiAgent.js               # Multi-agent orchestration
│   ├── ProcessHealth.js            # Health monitoring
│   ├── Retry.js                    # Retry logic
│   ├── ErrorHandler.js             # Error processing
│   ├── Debug.js                    # Debugging utilities
│   └── Injection.js                # Injection scheduling
├── test/                           # Test files (all parallel)
│   ├── bluetoothphone.test.js      # Bluetooth phone tests
│   ├── error.test.js               # Error handling and process isolation tests
│   ├── injection.test.js           # Core injection functionality
│   ├── keyboard.test.js            # Keyboard tests
│   ├── launcher.test.js            # Launcher tests
│   ├── systemservice.test.js       # System service tests
│   └── vehiclesetting.test.js      # Vehicle setting tests
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

## Manual Testing

**Note**: `frida-inject` is required for passing parameters to agents.

**macOS/Linux:**
```bash
# List running processes
frida-ps

# Inject agent manually with parameters
frida-inject -n com.qinggan.app.launcher -s ../voboost-script/build/weather-widget-mod.js --parameters='{"key":"value"}'
```

**Windows:**
```powershell
# List running processes
frida-ps

# Inject agent manually with parameters
frida-inject -n com.qinggan.app.launcher -s ..\voboost-script\build\weather-widget-mod.js --parameters='{\"key\":\"value\"}'
```

## Troubleshooting

### Common Issues

#### Java Bridge Errors on ARM64 macOS
```bash
# Increase delays between injections
export AGENT_DELAY=3000
npm test
```

#### Memory Issues in Multi-Agent Tests
```javascript
// In test configuration:
await Utils.runMultipleAgentsTest(t, 'launcher', agents, {
    maxMemoryMb: 1024,
    agentDelay: 3000
});
```

#### Process Cleanup Issues
```bash
# Check for orphaned processes
ps aux | grep java | grep Stub
# Clean up if necessary
pkill -f "java.*Stub"
```

### Debug Mode
```bash
# Enable debug logging
DEBUG=1 npm test
```

## Code Style

This project follows the unified Voboost code style from [voboost-codestyle](../voboost-codestyle).

For AI agent rules and coding guidelines, see [AGENTS.md](AGENTS.md).

### Linting and Formatting

```bash
npm run lint      # Fix all JS and Java files
npm run lint:js   # Fix JS files only (ESLint + Prettier)
npm run lint:java # Fix Java files only (checkstyle)
```

### Rules
- **JavaScript**: 100 char line, 4 spaces, single quotes, console only in DEBUG mode
- **Java**: Checkstyle configuration from voboost-codestyle


## License

Dual-licensed:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE) — free for personal use
- [Commercial license](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.md) — required otherwise
