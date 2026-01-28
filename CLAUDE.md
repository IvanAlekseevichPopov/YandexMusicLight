# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Описание проекта

Музыкальное приложение для Android на основе API Yandex Music.

## Технологический стек

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- Retrofit + OkHttp
- Gradle с Kotlin DSL
- Min SDK: 26, Target SDK: 34

## Команды сборки

```bash
./gradlew assembleDebug      # Сборка debug
./gradlew assembleRelease    # Сборка release
./gradlew test               # Тесты
./gradlew lint               # Линтер
```

## Модульная структура

```
:app                 — точка входа, навигация
:core:network        — Retrofit API клиент
:core:data           — репозитории, кэширование
:core:ui             — тема, общие компоненты
:feature:home        — экран "Главная"
:feature:search      — экран "Поиск"
:feature:library     — экран "Моя музыка"
```

## Документация модулей

| Модуль | README | Назначение |
|--------|--------|------------|
| `:app` | [app/README.md](app/README.md) | Точка входа, навигация |
| `:core:network` | [core/network/README.md](core/network/README.md) | API клиент Yandex Music |
| `:core:data` | [core/data/README.md](core/data/README.md) | Репозитории данных |
| `:core:ui` | [core/ui/README.md](core/ui/README.md) | Тема и UI компоненты |
| `:feature:home` | [feature/home/README.md](feature/home/README.md) | Экран главной |
| `:feature:search` | [feature/search/README.md](feature/search/README.md) | Экран поиска |
| `:feature:library` | [feature/library/README.md](feature/library/README.md) | Экран библиотеки |

## Граф зависимостей

```
:app
 ├── :feature:home ──┬── :core:ui
 ├── :feature:search ┼── :core:data ── :core:network
 └── :feature:library┘
```

## API Reference

Полная документация Yandex Music API: [API_REFERENCE.md](API_REFERENCE.md)

**Base URL:** `https://api.music.yandex.net`

**Аутентификация:** `Authorization: OAuth {token}`

## Переменные окружения

В файле `local.properties` (не коммитить):
```
YANDEX_TOKEN=ваш_токен
```

## Правила для ИИ-агента

1. **Изоляция модулей** — бизнес-логика в feature-модулях, не в :app
2. **Зависимости** — feature зависит от core, не наоборот
3. **API** — все сетевые вызовы через :core:network
4. **UI компоненты** — переиспользуемые компоненты в :core:ui
5. **Перед работой** — читать README соответствующего модуля
