# Модуль :core:network

Сетевой слой приложения. Работа с Yandex Music API.

## Назначение

- Retrofit интерфейс для Yandex Music API
- HTTP клиент с нужными заголовками
- Модели данных API (DTO)

## API Reference

Полная документация API: [API_REFERENCE.md](../../API_REFERENCE.md)

**Base URL:** `https://api.music.yandex.net`

**Обязательные заголовки:**
- `Authorization: OAuth {token}`
- `X-Yandex-Music-Client: YandexMusicAndroid/24023621`

## Ключевые файлы

| Файл | Описание |
|------|----------|
| `YandexMusicApi.kt` | Retrofit интерфейс с эндпоинтами API |
| `ApiClient.kt` | Singleton HTTP клиента |

## Реализованные эндпоинты

- `GET /account/status` — статус аккаунта
- `GET /search` — поиск
- `GET /landing3/chart` — чарт
- `GET /feed` — фид рекомендаций
- `GET /artists/{id}/brief-info` — информация об артисте

## Задачи для ИИ-агента

При добавлении нового эндпоинта:
1. Добавить метод в `YandexMusicApi.kt`
2. Создать DTO классы для response
3. Документация эндпоинта в `API_REFERENCE.md`

**Не делать:**
- Не хранить токен в этом модуле
- Не кэшировать данные здесь (это задача :core:data)
