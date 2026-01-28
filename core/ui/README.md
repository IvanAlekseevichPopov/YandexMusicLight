# Модуль :core:ui

Общие UI компоненты и тема приложения.

## Назначение

- Material 3 тема в стилистике Яндекс.Музыки
- Переиспользуемые Compose компоненты
- Цветовая палитра

## Ключевые файлы

| Файл | Описание |
|------|----------|
| `theme/Color.kt` | Цветовая палитра |
| `theme/Theme.kt` | `YandexMusicTheme` — Material 3 тема |

## Цветовая схема

```kotlin
Yellow = #FFCC00      // Primary (акцент Яндекса)
YellowDark = #E6B800  // Secondary
Black = #000000
White = #FFFFFF
DarkGray = #1A1A1A    // Background (dark mode)
LightGray = #F5F5F5   // Surface (light mode)
```

## Использование темы

```kotlin
@Composable
fun MyScreen() {
    YandexMusicTheme {
        // Контент с применённой темой
    }
}
```

## Задачи для ИИ-агента

При работе с этим модулем:
- Все общие компоненты (кнопки, карточки, списки) добавлять сюда
- Feature-модули импортируют тему отсюда
- Coil подключен для загрузки изображений

**Планируемые компоненты:**
- [ ] TrackListItem — элемент списка треков
- [ ] AlbumCard — карточка альбома
- [ ] ArtistCard — карточка артиста
- [ ] PlayButton — кнопка воспроизведения
