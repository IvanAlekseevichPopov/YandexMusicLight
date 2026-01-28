# Модуль :core:data

Слой данных. Репозитории, кэширование, маппинг DTO в domain-модели.

## Назначение

- Репозитории для работы с данными
- Преобразование DTO -> Domain модели
- Кэширование (TODO)
- Обработка ошибок сети

## Зависимости

```
:core:network — сетевой слой
```

## Ключевые файлы

| Файл | Описание |
|------|----------|
| `MusicRepository.kt` | Основной репозиторий для работы с API |

## Пример использования

```kotlin
val repository = MusicRepository(token = "OAuth token")

// Поиск
val searchResult = repository.search("Queen")

// Чарт
val chart = repository.getChart()

// Информация об артисте
val artist = repository.getArtistInfo(artistId = 79215)
```

## Задачи для ИИ-агента

При работе с этим модулем:
- Все методы возвращают `Result<T>` для обработки ошибок
- Сетевые вызовы выполняются на `Dispatchers.IO`
- При добавлении кэширования использовать Room

**Планируемые улучшения:**
- [ ] Room для оффлайн кэша
- [ ] Domain модели отдельно от DTO
- [ ] UseCase классы
