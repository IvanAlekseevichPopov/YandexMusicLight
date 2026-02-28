# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Описание проекта

Музыкальное приложение для Android на основе API Yandex Music. Реализовано: OAuth-авторизация (логин/пароль с поддержкой captcha и SMS challenge), Home-экран с фидом, воспроизведение треков через ExoPlayer с прогресс-баром. Search и Library — заглушки.

## Разрешения агента

Агент имеет полные права:
- **Запись/редактирование** любых файлов внутри этого репозитория
- **Создание/удаление** файлов и директорий
- **Поиск в интернете** для получения актуальной документации, примеров, решений
- **Запуск Gradle** (`./gradlew assembleDebug`, `./gradlew test`, `./gradlew lint`)
- **Git-операции** (checkout, commit, branch) — по запросу пользователя

## Команды сборки

```bash
./gradlew assembleDebug      # Сборка debug APK
./gradlew assembleRelease    # Сборка release APK
./gradlew test               # Юнит-тесты
./gradlew lint               # Android Lint
```

## Модульная структура

```
:app                 — точка входа (MainActivity), навигация, DI
:core:network        — Retrofit API клиент (ApiClient, AuthApiClient), DTO модели
:core:data           — MusicRepository, PlayerService, AuthRepository, TokenStorage
:core:ui             — Material 3 тема (Yandex Yellow #FFCC00), Coil
:feature:auth        — LoginScreen, ChallengeScreen, AuthViewModel (OAuth-флоу)
:feature:home        — HomeScreen + HomeViewModel, AudioPlayer (ExoPlayer)
:feature:search      — SearchScreen (заглушка)
:feature:library     — LibraryScreen (заглушка)
```

## Граф зависимостей

```
:app
 ├── :feature:auth ──┬── :core:ui
 ├── :feature:home ──┼── :core:data ── :core:network
 ├── :feature:search ┤
 └── :feature:library┘
```

## Архитектурные паттерны

- **State:** `StateFlow<UiState>` + `ViewModel`, `collectAsState()` в Composable
- **Async:** `suspend fun` + `Result<T>` + `Dispatchers.IO`
- **DI:** Ручная инициализация в MainActivity (планируется Hilt)
- **Плеер:** ExoPlayer (Media3) в AudioPlayer обёртке, прогресс через polling каждые 500мс

## Аутентификация

Документация флоу: [AUTH_FLOW.md](AUTH_FLOW.md)

**Два варианта:**
- **Flow A** (простой): логин → пароль → session cookies → X-Token → Music Token
- **Flow B** (challenge): логин → пароль → captcha → SMS → session → X-Token → Music Token

**Хранение:** `EncryptedSharedPreferences` (AES-256) в `TokenStorage`

**API заголовок:** `Authorization: OAuth {token}`

**Ключевые классы:**
- `AuthApiClient` — OkHttp с CookieJar для passport.yandex.ru
- `AuthRepository` — оркестрация всех шагов auth-флоу
- `AuthViewModel` — UI-стейт машина (Login → Captcha → SMS → ExchangingTokens → Success)

## API Reference

Полная документация: [API_REFERENCE.md](API_REFERENCE.md)

**Base URL:** `https://api.music.yandex.net`

**Обязательный заголовок:** `X-Yandex-Music-Client: YandexMusicAndroid/24023621`

## Воспроизведение

**Получение ссылки** (PlayerService, 3 шага):
1. `GET /tracks/{id}/download-info` → варианты codec/bitrate
2. `GET {downloadInfoUrl}` → XML (host, path, ts, s)
3. URL: `https://{host}/get-mp3/{MD5(salt+path+s)}/{ts}{path}`

**Плеер** (AudioPlayer → ExoPlayer):
- `play(url)` → MediaItem → prepare → play
- Прогресс: polling каждые 500мс → HomeUiState.progress (0.0..1.0)

## TODO

[TODO.md](TODO.md)

## Правила для ИИ-агента

1. **Изоляция модулей** — бизнес-логика в feature-модулях, не в :app
2. **Зависимости** — feature зависит от core, не наоборот
3. **API** — все сетевые вызовы через :core:network, обёрнуты в `Result<T>` в :core:data
4. **UI компоненты** — переиспользуемые компоненты в :core:ui
5. **Перед работой** — читать README соответствующего модуля
6. **State** — `StateFlow<UiState>` + `ViewModel` в feature-модулях
7. **DTO** — `@SerializedName` для Gson, классы в `dto/` пакетах :core:network
8. **Обложки** — `cover_uri` содержит `%%`, заменять на `{size}x{size}` (200, 400, 600...)
9. **Не коммитить** — `local.properties`, токены, cookie-строки
