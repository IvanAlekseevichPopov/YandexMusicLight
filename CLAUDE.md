# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Описание проекта

Музыкальное приложение для Android на основе API Yandex Music.

## Технологический стек

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose для навигации
- Gradle с Kotlin DSL
- Минимальный SDK: 26 (Android 8.0)
- Target SDK: 34

## Команды сборки

```bash
# Сборка debug-версии
./gradlew assembleDebug

# Сборка release-версии
./gradlew assembleRelease

# Запуск тестов
./gradlew test

# Проверка линтером
./gradlew lint
```

## Структура проекта

```
app/src/main/java/com/example/yandexmusic/
├── MainActivity.kt          # Точка входа
├── navigation/              # Навигация приложения
├── ui/                      # UI компоненты и экраны
│   ├── screens/            # Экраны приложения
│   └── theme/              # Тема Material 3
└── data/                    # Работа с данными и API
```

## Yandex Music API

- API неофициальный, базовый URL: `api.music.yandex.net`
- Требуется токен аутентификации от аккаунта Яндекс
- Токен хранить в `local.properties` (не коммитить)

## Переменные окружения

В файле `local.properties`:
```
YANDEX_TOKEN=ваш_токен
```
