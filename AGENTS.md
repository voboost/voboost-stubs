# Voboost Stubs Code Style (CRITICAL)

## Global
- This project MUST follows ALL common rules from ../voboost-codestyle/AGENTS.md

# Commands
- `npm test`: Run test suite
- `npm test:verbose`: Run tests in verbose mode
- `npm test:*`: Run specific tests
- `npm start`: Build all stub JARs and start all stub applications
- `npm start:*`: Start specific stub application
- `npm run lint`: Fix all JS and Java files
- `npm run lint:js`: Fix JS files only (ESLint + Prettier)
- `npm run lint:java`: Fix Java files only (checkstyle)
- `npm run build`: Build Java stub applications

# Project Structure
- **apps/**: Java stub applications
- **lib/**: Consolidated test infrastructure
- **config/**: Configuration files
  - `config-ava.mjs`: AVA test configuration
  - `config-eslint.mjs`: ESLint configuration
- **test/**: Test files
