# TODO

## Фичи
- [ ] Search: полная реализация (ViewModel, UI, debounce 300ms)
- [ ] Library: полная реализация (лайки, плейлисты, альбомы)
- [ ] Реальное аудио-воспроизведение (ExoPlayer/Media3)
- [ ] UI-компоненты: TrackListItem, AlbumCard, ArtistCard, PlayerBar
- [ ] Загрузка обложек через Coil (шаблон `%%` → размер)
- [ ] Пагинация списков

## Архитектура
- [ ] DI (Hilt) вместо ручной инициализации
- [ ] Вынести Cookie из хардкода в secure storage
- [ ] Room для оффлайн-кэша
- [ ] Domain-слой (UseCases)

## Инфра
- [ ] ProGuard правила для Retrofit/Gson
- [ ] Тесты
