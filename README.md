# ZavGar

Мобильное приложение бонусной системы для Android и iOS. Общая логика и интерфейс написаны на Kotlin Multiplatform и Compose Multiplatform. В проекте есть экраны регистрации и входа, кошелька, истории операций, профиля и настроек.

## Требования

- JDK 17 и Android SDK с платформой API 37 для сборки Android.
- Android Studio с поддержкой Kotlin Multiplatform для работы над общим кодом.
- macOS и Xcode для сборки iOS; минимальная версия iOS в проекте — 18.2.
- Доступ к Maven Central, Google Maven и Gradle Plugin Portal при первой загрузке зависимостей.

Версии Kotlin, Compose, Ktor и остальных зависимостей заданы в [`gradle/libs.versions.toml`](gradle/libs.versions.toml). Gradle запускается через wrapper, устанавливать его отдельно не нужно.

## Быстрый старт: Android

Для запуска без бэкенда выберите в Android Studio вариант сборки `mockDebug` в **Build Variants** и запустите модуль `app`. Из терминала можно собрать APK:

```shell
./gradlew :app:assembleMockDebug
```

Вариант `mockDebug` использует встроенный Ktor `MockEngine` и заготовленные ответы. Для работы с реальным сервером выберите `prodDebug` или выполните:

```shell
./gradlew :app:assembleProdDebug
```

Debug APK создаются в `app/build/outputs/apk/`. Оба варианта можно установить одновременно: у `mock` отдельный суффикс `applicationId`. Debug-конфигурации Firebase для Android уже находятся в `app/src/debug/` и `app/src/mock/`.

Сетевой адрес задаётся реализацией `AppConfig` в `libraries/firebase`: debug-сборка использует адрес по умолчанию, release-сборка может получить `base_url` из Firebase Remote Config. Чтобы направить `prodDebug` на другой сервер, измените соответствующий `defaultBaseUrl` для платформы.

## Запуск на iOS

Откройте `iosApp/iosApp.xcodeproj` в Xcode, выберите схему `iosApp` и симулятор или устройство, затем запустите приложение. Xcode собирает и подключает общий Kotlin-фреймворк через Gradle-задачу `:shared:embedAndSignAppleFrameworkForXcode`.

По умолчанию iOS обращается к реальному серверу. Для локального запуска на заготовленных ответах установите `useMockServer: true` в вызове `doInitKoinIos` в [`iosApp/iosApp/ZavGarApp.swift`](iosApp/iosApp/ZavGarApp.swift). Для запуска требуется настроенная конфигурация Firebase; проект Xcode копирует `GoogleService-Info.plist` из `iosApp/Firebase/Debug/` или `iosApp/Firebase/Release/` в зависимости от конфигурации сборки.

## Структура проекта

| Каталог | Назначение |
| --- | --- |
| `app/` | Android-приложение, варианты `prod` и `mock`, точка входа Compose. |
| `iosApp/` | iOS-приложение на SwiftUI, встраивающее общий интерфейс. |
| `shared/` | Общая точка сборки приложения и граф зависимостей Koin. |
| `features/` | Экраны и пользовательские сценарии; `features/navigation` реализует навигацию. |
| `domain/` | Модели и сценарии предметной области. |
| `data/` | Репозитории, DataStore, сетевой клиент и mock-сервер. |
| `libraries/` | Общие компоненты: дизайн-система, конфигурация, Firebase, события и контракты навигации. |
| `core/`, `utils/`, `resources/` | UI-помощники, общие утилиты и Compose-ресурсы. |
| `plugins/` | Собственные Gradle-плагины и настройки KMP-модулей. |

Основные технологии: Kotlin Multiplatform, Compose Multiplatform, Koin, Ktor, DataStore, Firebase и Navigation 3. Версии и полный состав модулей смотрите в каталоге зависимостей и [`settings.gradle.kts`](settings.gradle.kts).

## Проверки

```shell
./gradlew detekt
./gradlew :app:assembleProdDebug
./gradlew testAndroidHostTest :app:testProdDebugUnitTest
./gradlew :app:lintProdDebug
./gradlew koverHtmlReport
```

`testAndroidHostTest` запускает unit-тесты KMP-модулей. CI на каждый pull request и push в `main` выполняет Detekt, сборку `prodDebug`, unit-тесты и формирует XML-отчёт Kover. Конфигурация находится в [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

## Release-сборка Android

Для подписанного release APK нужны `config/signing/signing.properties` с ключами `keyAlias`, `keyPassword`, `storePath`, `storePassword` **или** переменные окружения `ZAVGAR_KEY_ALIAS`, `ZAVGAR_KEY_PASSWORD`, `ZAVGAR_STORE_PATH`, `ZAVGAR_KEY_STORE_PASSWORD`. Также нужна конфигурация Firebase для release-варианта Android. Файлы подписи исключены из Git.

```shell
./gradlew :app:assembleProdRelease
```
