# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Описание проекта

Музыкальное приложение для Android на основе API Yandex Music. Реализовано: OAuth-авторизация (логин/пароль с поддержкой captcha и SMS challenge), Home-экран с фидом/чартом, воспроизведение треков через ExoPlayer с прогресс-баром и навигацией (prev/next), предзагрузка следующего трека на диск, Library — список скачанных треков с оффлайн-воспроизведением. Search — заглушка.

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
:app                 — точка входа (MainActivity, MusicApp), навигация, DI (singleton'ы в Application)
:core:network        — Retrofit API клиент (ApiClient, AuthApiClient, downloadClient), DTO модели
:core:data           — MusicRepository, PlayerService, AuthRepository, TokenStorage, TrackDownloadManager
                       db/ — Room (AppDatabase, DownloadedTrackEntity, DownloadedTrackDao)
:core:ui             — Material 3 тема (Yandex Yellow #FFCC00), Coil
:feature:auth        — LoginScreen, ChallengeScreen, AuthViewModel (OAuth-флоу)
:feature:home        — HomeScreen + HomeViewModel, AudioPlayer (ExoPlayer), предзагрузка треков
:feature:search      — SearchScreen (заглушка)
:feature:library     — LibraryScreen + LibraryViewModel (список скачанных треков)
```

## Граф зависимостей

```
:app
 ├── :feature:auth ──┬── :core:ui
 ├── :feature:home ──┼── :core:data ── :core:network
 ├── :feature:search ┤       │
 └── :feature:library┘       ├── TrackDownloadManager (preload + permanent downloads)
                             ├── PlayerService (URL resolution, 3-step)
                             ├── MusicRepository (API wrapper)
                             ├── TokenStorage (EncryptedSharedPreferences)
                             └── db/ (Room: AppDatabase, DAO, Entity)
```

## Потоки данных

**Воспроизведение с предзагрузкой:**
```
HomeViewModel.playTrack(id)
  → TrackDownloadManager.getCachedTrack(id)     // файл на диске?
  ├─ есть  → AudioPlayer.play(file:///...)      // мгновенный старт
  └─ нет   → PlayerService.getTrackUrl(id)      // 3-step URL resolution
            → AudioPlayer.play(https://...)      // стриминг
  → triggerPreloadNext()                         // фоновое скачивание N+1
```

**Скачивание трека:**
```
TrackDownloadManager.downloadTrack(id, title, artist)
  → PlayerService.getTrackUrl(id)               // ephemeral URL
  → OkHttp downloadClient → файл на диск        // filesDir/downloads/
  → Room DAO.insert(entity)                      // метаданные в БД
```

## Архитектурные паттерны

- **State:** `StateFlow<UiState>` + `ViewModel`, `collectAsState()` в Composable
- **Async:** `suspend fun` + `Result<T>` + `Dispatchers.IO`
- **DI:** Lazy singleton'ы в `MusicApp` (Application), ViewModels через `remember` в Composable
- **Плеер:** ExoPlayer (Media3) в AudioPlayer обёртке, прогресс через polling каждые 500мс
- **БД:** Room (KSP, `room.generateKotlin = true` для совместимости с JDK 21)
- **Кэш:** Двухуровневый — preload (`cacheDir`, временный) + permanent (`filesDir`, Room метаданные)
- **401:** Интерцептор в ApiClient → `onUnauthorized` → очистка токенов → экран авторизации

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
- `play(url)` → MediaItem → prepare → play (поддерживает `https://` и `file:///`)
- Навигация: prev/next по trackList, автопереход на следующий трек
- Прогресс: polling каждые 500мс → HomeUiState.progress (0.0..1.0)

**Предзагрузка** (TrackDownloadManager):
- Preload cache: `cacheDir/tracks/{trackId}.mp3` (временный, ОС может удалить)
- Permanent downloads: `filesDir/downloads/{trackId}.mp3` (постоянный, Room метаданные)
- При воспроизведении трека — фоновое скачивание следующего

## Хранение данных

- **Токены:** `EncryptedSharedPreferences` (AES-256) в `TokenStorage`
- **Скачанные треки:** Room `downloaded_tracks` (trackId, title, artistName, codec, bitrate, fileName, fileSize, downloadedAt)
- **Preload cache:** `cacheDir/tracks/{trackId}.mp3` — автоочистка при инициализации TrackDownloadManager, ОС может удалить при нехватке места
- **Downloads:** `filesDir/downloads/{trackId}.mp3` — постоянное хранение, управляется через Library экран

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
10. **Room** — KSP с `room.generateKotlin = true`, миграции через fallbackToDestructiveMigration пока version=1
11. **Скачивание** — URL эфемерные (ts+sign), скачивать сразу после resolve; `downloadClient` без auth-заголовков; запись в `.tmp` → rename (атомарность); `suspendCancellableCoroutine` для отмены HTTP-запросов
12. **Файловые URI** — использовать `Uri.fromFile(file).toString()` (не `File.toURI()`), ExoPlayer принимает `file:///`
