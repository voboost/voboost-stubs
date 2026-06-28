## Context

Voboost-stubs currently provides 5 JVM main() classes that mock vehicle infotainment processes for Frida injection testing. These host-side stubs don't replicate Android process behavior, lifecycle, or security boundaries that Frida encounters in production.

The project structure is a single JVM Gradle project with `com.gradle.application` plugin and host-side test execution. Each stub is a main() class that exits immediately after startup, providing no persistent process target for injection.

## Goals / Non-Goals

**Goals:**
- Transform 5 JVM stubs into Android APK modules that run as real Android processes
- Each APK runs a foreground service to keep the process alive for Frida injection
- Enable realistic device testing matching production Frida targets
- Maintain stub simplicity - no complex UI or business logic, just persistent processes
- Support arm64-v8a architecture only (matching target vehicle hardware)

**Non-Goals:**
- Complex application UI or user interfaces beyond basic service display
- Full replication of production application functionality (stubs remain minimal)
- Architectural changes to the stub process behavior (keep simple, persistent targets)
- Multi-architecture support beyond arm64-v8a (vehicle hardware is fixed)

## Decisions

### D1. Multi-module Android build structure
Convert from single JVM project to multi-module Android Gradle project. Each stub becomes a separate `com.android.application` module with its own `build.gradle.kts`. This mirrors real Android application structure and allows independent APK building/installation. *Rejected:* single APK with multiple processes - more complex and doesn't match real application architecture.

### D2. Android 9 (API 28) baseline, arm64-v8a only
Target Android 9 (API 28) as minimum SDK, arm64-v8a architecture only. This matches the vehicle infotainment system requirements and reduces build complexity. *Rejected:* multi-architecture or older API levels - adds complexity without benefit for fixed target hardware.

### D3. Foreground service for process persistence  
Each APK runs a foreground service to keep the process alive for Frida injection. Android will otherwise kill background processes, making injection unreliable. The foreground service shows a persistent notification to prevent system termination. *Rejected:* background service or no service - processes would be killed by Android, unreliable for injection.

### D4. applicationId matches target process name
Each APK's applicationId matches the real vehicle process name (e.g., `com.android.launcher3`, `com.android.bluetoothphone`). This ensures Frida can target the correct process name and matches production injection configuration. *Rejected:* arbitrary applicationIds - would require injection configuration changes.

### D5. Legacy mock Android classes removed
The current stubs have mock Android classes that don't replicate real Android behavior. Remove these in favor of real Android framework dependencies. This ensures realistic process behavior and eliminates mock maintenance burden. *Rejected:* keeping mocks - they don't provide realistic Android process behavior.

### D6. Host-side tests NOT ported
Current host-side JVM tests are not portable to Android. Device-side Android tests will be added as needed for basic functionality verification. Complex testing is handled by the injection system itself. *Rejected:* porting host tests - they test JVM behavior, not Android process behavior.

## Risks / Trade-offs

- [Increased build complexity] → Multi-module Android build is more complex than single JVM project, but matches real Android development
- [Device dependency] → Testing requires Android device/emulator, but provides realistic injection targets  
- [Foreground service notifications] → Each stub shows a persistent notification, but ensures process reliability for injection
- [Removed mock classes] → Some existing host-side tests may break, but mocks didn't provide realistic Android behavior

## Migration Plan

Implementation follows the task breakdown in `tasks.md`. Key phases:
1. Multi-module Android build conversion (build system)
2. Individual stub module implementation (5 APK modules)
3. Build verification and testing
4. Documentation updates

Rollback: Previous JVM stub code remains in git history if needed.
