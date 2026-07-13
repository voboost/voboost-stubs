# voboost-stubs

[English](README.md) | **Русский**

Android stub-APK приложения, служащие реалистичными целями для Frida-инъекций
при разработке и тестировании системы Voboost.

## Обзор

Voboost-stubs предоставляет набор минимальных Android APK-модулей, реплицирующих
имена процессов и базовую структуру реальных приложений информационно-развлекательной
системы автомобиля. Каждый стаб работает как реальный Android-процесс (с foreground-сервисом,
удерживающим его живым), так что Frida-агенты из [`voboost-script`](../voboost-script)
могут внедряться против тех же имён процессов, жизненного цикла и границ безопасности,
что и в production.

- Каждый стаб — отдельный Gradle-модуль `com.android.application`
- `applicationId` каждого APK совпадает с реальным именем процесса автомобиля
- Каждый APK запускает foreground-сервис, чтобы процесс оставался живым для инъекции
- Цель — Android 9 (API 28), только `arm64-v8a` (соответствует аппаратуре автомобиля)

Этот репозиторий — часть платформы Voboost (см.
[Связь с другими репозиториями](#связь-с-другими-репозиториями) ниже).

## Архитектура

Проект предоставляет multi-module Android APK stub-цели для тестирования Frida-инъекций.
Каждый стаб живёт в собственной директории верхнего уровня (`launcher/`,
`bluetoothphone/`, `systemservice/`, `qgime/`, `vehiclesetting/`) и собирает
устанавливаемый APK, чей `applicationId` совпадает с реальным именем процесса автомобиля.

Дизайн и обоснование этой архитектуры задокументированы как OpenSpec-изменение в
[`openspec/changes/android-apk-port/`](openspec/changes/android-apk-port/).

## Предварительные требования

### Общие

- **JDK 17** (требуется Android Gradle Plugin)
- **Android SDK** с platform 35 и build-tools (установите `ANDROID_HOME`)
- **Node.js** (для JS-набора тестов и линтинга)

### macOS

```bash
brew install openjdk@17 node
# Android SDK через Android Studio или sdkmanager
pip3 install frida-tools   # для ручной инъекции
```

### Windows

- JDK 17 из [Adoptium](https://adoptium.net/) (установите `JAVA_HOME`)
- Android Studio для SDK
- Node.js из [nodejs.org](https://nodejs.org/)
- `pip install frida-tools` для ручной инъекции

Установите JS-зависимости один раз:

```bash
npm install
```

## Доступные стабы

| Модуль | applicationId / имя процесса | Назначение |
|--------|------------------------------|-----------|
| `launcher` | `com.qinggan.app.launcher` | Процесс лаунчера |
| `bluetoothphone` | `com.qinggan.bluetoothphone` | Сервис Bluetooth-телефонии |
| `systemservice` | `com.qinggan.systemservice` | Системный сервис |
| `qgime` | `com.qinggan.app.qgime` | Редактор метода ввода |
| `vehiclesetting` | `com.qinggan.app.vehiclesetting` | Настройки автомобиля |

Каждый модуль выпускает release APK с именем `<module>.apk` (например `launcher.apk`)
в `<module>/build/outputs/apk/release/`.

## Сборка

Собрать все stub-APK:

```bash
./gradlew buildAllStubs
```

Собрать один стаб:

```bash
./gradlew :launcher:assemble
```

На Windows используйте `.\gradlew.bat` вместо `./gradlew`.

Release-вариант debuggable при передаче `-Pdebuggable=true`; по умолчанию debug-вариант
отключен и собирается только release.

## Развёртывание на устройстве

```bash
adb install -r launcher/build/outputs/apk/release/launcher.apk
adb shell am start -n com.qinggan.app.launcher/.LauncherActivity
```

Foreground-сервис удерживает процесс живым, чтобы Frida могла подключиться по имени
процесса.

## Тестирование

JS-набор тестов (AVA) внедряет агенты из `voboost-script` в stub-процессы и валидирует
поведение. Используется PID-инъекция для полной параллелизации.

```bash
npm test                 # запустить все тесты
npm run test:launcher    # stub launcher
npm run test:phone       # stub bluetoothphone
npm run test:system      # stub systemservice
npm run test:keyboard    # stub qgime
npm run test:vehicle     # stub vehiclesetting
```

Тестовая инфраструктура — в [`lib/`](lib/) (`Utils.js`, `Frida.js`, `Fixtures.js`,
`MultiAgent.js`, `ProcessHealth.js`, `Retry.js`, `ErrorHandler.js`, `Debug.js`,
`Injection.js`).

### Ручная инъекция

`frida-inject` требуется для передачи параметров агентам:

```bash
frida-ps
frida-inject -n com.qinggan.app.launcher \
  -s ../voboost-script/build/weather-widget-mod.js \
  --parameters='{"key":"value"}'
```

## Структура проекта

```
voboost-stubs/
├── launcher/            # Android APK stub-модуль (com.qinggan.app.launcher)
├── bluetoothphone/      # Android APK stub-модуль (com.qinggan.bluetoothphone)
├── systemservice/       # Android APK stub-модуль (com.qinggan.systemservice)
├── qgime/               # Android APK stub-модуль (com.qinggan.app.qgime)
├── vehiclesetting/      # Android APK stub-модуль (com.qinggan.app.vehiclesetting)
├── lib/                 # JS тестовая инфраструктура (модульная)
├── test/                # файлы тестов AVA (параллельные)
├── config/              # конфигурация AVA + ESLint
├── openspec/            # spec-driven изменения (android-apk-port)
├── build.gradle.kts     # корневая multi-module Android сборка
├── settings.gradle.kts  # подключает 5 APK-модулей
└── gradle.properties    # AndroidX + настройки Gradle
```

## Связь с другими репозиториями

Voboost — мультирепозиторная платформа:

- [`voboost`](../voboost) — Android-приложение (UI, OTA-клиент, управление устройством).
- [`voboost-inject`](../voboost-inject) — root-демон (Vala), внедряющий Frida-агентов в
  процессы автомобиля и самообновляющийся через APK-level OTA.
- [`voboost-script`](../voboost-script) — JS-модули Frida-агентов, внедряемые в целевые
  процессы.
- **`voboost-stubs`** (этот репо) — Android APK-стабы, заменяющие реальные процессы
  автомобиля при разработке и тестировании.
- [`voboost-install`](../voboost-install) — инструменты установки на устройство.

Стабы позволяют прорабатывать `voboost-inject` и `voboost-script` против реальных имён
Android-процессов и жизненных циклов без production-прошивки автомобиля.

## Стиль кода

Этот проект следует единому стилю кода Voboost из
[`voboost-codestyle`](../voboost-codestyle). Правила для AI-агентов и руководства по
кодированию — в [AGENTS.md](AGENTS.md).

```bash
npm run lint        # исправить все JS и Java файлы
npm run lint:js     # только JS (ESLint + Prettier)
npm run lint:java   # только Java (checkstyle)
```

Правила:

- **JavaScript**: строка 100 символов, 4 пробела, одинарные кавычки, console только в DEBUG
- **Java**: конфигурация Checkstyle из voboost-codestyle

## Устранение проблем

### Проблемы сборки Android

Убедитесь, что `ANDROID_HOME` указывает на SDK с platform 35 и установленными build-tools.
Сборка targeting только `arm64-v8a`; x86-эмулятор не установит выходные APK — используйте
arm64-образ.

### Потерянные stub-процессы

```bash
adb shell ps | grep qinggan
adb shell am force-stop com.qinggan.app.launcher
```

### Отладочное логирование

```bash
DEBUG=1 npm test
```

## Лицензия

Двойная лицензия:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE.ru.md) — бесплатно для личного использования
- [Коммерческая лицензия](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.ru.md) — требуется для любого коммерческого использования
