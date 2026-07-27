## 1. OpenSpec setup (android-apk-port)

- [x] 1.1 Create openspec directory structure for voboost-stubs repository
- [x] 1.2 Create openspec/project.md describing voboost-stubs purpose and migration
- [x] 1.3 Create android-apk-port change with .openspec.yaml (schema: spec-driven, created: 2026-06-26)
- [x] 1.4 Create proposal.md describing the JVM-to-APK port rationale and scope
- [x] 1.5 Create design.md with current state, target state, and build system transformation decisions
- [x] 1.6 Create tasks.md with implementation breakdown (this file)
- [x] 1.7 Create specs/android-apk-port/spec.md with ADDED Requirements and SHALL statements

## 2. Multi-module Android build conversion

- [x] 2.1 Update root build.gradle.kts from JVM application to multi-module Android project
- [x] 2.2 Configure Android build plugin repositories and dependencies
- [x] 2.3 Create shared build configuration for Android SDK and build tools versions
- [x] 2.4 Update settings.gradle.kts to include 5 new Android modules
- [x] 2.5 Configure common Android manifest permissions and shared dependencies

## 3. Individual stub module implementation

- [x] 3.1 Create launcher Android APK module with foreground service
- [x] 3.2 Create bluetoothphone Android APK module with foreground service
- [x] 3.3 Create systemservice Android APK module with foreground service
- [x] 3.4 Create qgime Android APK module with foreground service
- [x] 3.5 Create vehiclesetting Android APK module with foreground service
- [x] 3.6 Ensure each module uses correct applicationId matching target process name

## 4. Build verification

- [x] 4.1 Verify each module builds successfully as Android APK
- [ ] 4.2 Verify APK installation on Android device/emulator via adb
- [ ] 4.3 Verify foreground service keeps each process alive for injection
- [ ] 4.4 Verify process names match expected injection targets
- [ ] 4.5 Test Frida injection against each stub APK

## 5. Documentation updates

- [x] 5.1 Update README.md with Android APK build and deployment instructions
- [x] 5.2 Document each stub's purpose, applicationId, and process name
- [x] 5.3 Update AGENTS.md with new Android stub architecture
- [x] 5.4 Add troubleshooting guide for common Android build/deployment issues

## 6. Legacy code removal

- [x] 6.1 Delete legacy host-side JVM stub code under `apps/` (tracked) and `bin/` (build output)
- [x] 6.2 Remove `apps/` references from README.md, AGENTS.md, and openspec docs
- [x] 6.3 Verify `./gradlew buildAllStubs` still passes after legacy removal
