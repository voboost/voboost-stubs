# Voboost Stubs Code Style (CRITICAL)

## Global
- This project MUST follows ALL common rules from ../voboost-codestyle/AGENTS.md

# Commands
- `npm test`: Run test suite
- `npm test:verbose`: Run tests in verbose mode
- `npm test:*`: Run specific tests
- `./gradlew buildAllStubs`: Build all stub APKs
- `npm run lint`: Fix all JS and Java files
- `npm run lint:js`: Fix JS files only (ESLint + Prettier)
- `npm run lint:java`: Fix Java files only (checkstyle)

# Project Structure
- **launcher/**: Android APK stub module (com.qinggan.app.launcher)
- **bluetoothphone/**: Android APK stub module (com.qinggan.bluetoothphone)
- **systemservice/**: Android APK stub module (com.qinggan.systemservice)
- **qgime/**: Android APK stub module (com.qinggan.app.qgime)
- **vehiclesetting/**: Android APK stub module (com.qinggan.app.vehiclesetting)
- **lib/**: Consolidated test infrastructure
- **config/**: Configuration files
  - `config-ava.mjs`: AVA test configuration
  - `config-eslint.mjs`: ESLint configuration
- **test/**: Test files
