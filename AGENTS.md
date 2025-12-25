# Voboost Stubs Code Style (CRITICAL)

## Global
- This project MUST follows ALL common rules from ../voboost-codestyle/AGENTS.md

# Commands
- `npm test`: Run test suite
- `npm run test:verbose`: Run tests with verbose output
- `npm run lint`: Fix all JS and Java files
- `npm run lint:js`: Fix JS files only (ESLint + Prettier)
- `npm run lint:java`: Fix Java files only (checkstyle)

# Project Structure
- **apps/**: Java stub applications
  - `com/qinggan/`: Package structure with stub applications
- **lib/**: Consolidated test infrastructure (4 files)
  - `test-frida-helper.js`: Core Frida operations + Java bridge setup
  - `test-utils.js`: High-level test utilities + process utilities
  - `test-process-manager.js`: Process lifecycle management
  - `test-fixtures.js`: Test configurations and patterns
- **config/**: Configuration files
  - `config-ava.mjs`: AVA configuration
  - `config-eslint.mjs`: ESLint configuration
- **test/**: Test files
  - `bluetoothphone.test.js`: Bluetooth phone stub tests
  - `launcher.test.js`: Launcher stub tests
  - `qgime.test.js`: QGIME stub tests
  - `systemservice.test.js`: System service stub tests
  - `vehiclesetting.test.js`: Vehicle setting stub tests
