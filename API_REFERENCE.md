# Yandex Music API — Полная документация эндпоинтов

**Base URL:** `https://api.music.yandex.net`
**Авторизация:** Заголовок `Authorization: OAuth {token}`
**Заголовки:** `X-Yandex-Music-Client: YandexMusicAndroid/24023621`, `User-Agent: Yandex-Music-API`
**Таймаут по умолчанию:** 5 сек

---

## 1. Аккаунт

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 1 | `account_status()` | GET | `/account/status` | Получение статуса аккаунта (подписка, uid и т.д.) |
| 2 | `account_settings()` | GET | `/account/settings` | Получение настроек текущего пользователя |
| 3 | `account_settings_set()` | POST | `/account/settings` | Изменение настроек текущего пользователя |
| 4 | `account_experiments()` | GET | `/account/experiments` | Получение экспериментальных функций аккаунта |
| 5 | `consume_promo_code()` | POST | `/account/consume-promo-code` | Активация промо-кода |
| 6 | `settings()` | GET | `/settings` | Получение предложений по покупке (продукты) |
| 7 | `permission_alerts()` | GET | `/permission-alerts` | Получение оповещений |

**Детали:**

- **`account_settings_set`** — POST-данные: `{param: value}` или произвольный `data: dict`. Названия параметров — поля `UserSettings` в CamelCase.
- **`consume_promo_code`** — POST-данные: `{code: str, language: str}`. Языки: `en`, `uz`, `uk`, `us`, `ru`, `kk`, `hy`.

---

## 2. Фид (лента рекомендаций)

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 8 | `feed()` | GET | `/feed` | Получение фида (умные плейлисты, рекомендации) |
| 9 | `feed_wizard_is_passed()` | GET | `/feed/wizard/is-passed` | Прошёл ли пользователь визард (хэллоуин-событие) |

---

## 3. Лендинг и каталог

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 10 | `landing()` | GET | `/landing3` | Лендинг-страница с блоками (релизы, чарты, миксы и т.д.) |
| 11 | `chart()` | GET | `/landing3/chart` или `/landing3/chart/{option}` | Получение чарта (world, russia и др.) |
| 12 | `new_releases()` | GET | `/landing3/new-releases` | Полный список новых релизов (альбомов) |
| 13 | `new_playlists()` | GET | `/landing3/new-playlists` | Полный список новых плейлистов |
| 14 | `podcasts()` | GET | `/landing3/podcasts` | Список подкастов с лендинга |

**Детали:**

- **`landing`** — параметры: `blocks` (типы блоков: `personalplaylists`, `promotions`, `new-releases`, `new-playlists`, `mixes`, `chart`, `artists`, `albums`, `playlists`, `play_contexts`), `eitherUserId`.
- **`chart`** — `chart_option` — постфикс из поля `menu` чарта (напр. `world`, `russia`).

---

## 4. Жанры и теги

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 15 | `genres()` | GET | `/genres` | Получение всех жанров музыки |
| 16 | `tags()` | GET | `/tags/{tag_id}/playlist-ids` | Получение тега (подборки) с плейлистами |

---

## 5. Треки

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 17 | `tracks()` | POST | `/tracks` | Получение треков по ID |
| 18 | `tracks_download_info()` | GET | `/tracks/{track_id}/download-info` | Информация о вариантах загрузки трека |
| 19 | `track_supplement()` | GET | `/tracks/{track_id}/supplement` | Дополнительная информация о треке (устарело для текста) |
| 20 | `tracks_lyrics()` | GET | `/tracks/{track_id}/lyrics` | Получение текста трека |
| 21 | `tracks_similar()` | GET | `/tracks/{track_id}/similar` | Получение похожих треков |
| 22 | `play_audio()` | POST | `/play-audio` | Отправка состояния прослушивания трека |
| 23 | `after_track()` | GET | `/after-track` | Получение шота от Алисы / рекламы после трека |

**Детали:**

- **`tracks`** — POST-данные: `{track-ids: ids, with-positions: "True"/"False"}`.
- **`tracks_lyrics`** — GET-параметры: `{format: "TEXT"|"LRC", timeStamp: str, sign: str}`. Требует авторизацию и подпись запроса.
- **`play_audio`** — POST-данные: `{track-id, from-cache, from, play-id, uid, timestamp, track-length-seconds, total-played-seconds, end-position-seconds, album-id, playlist-id, client-now}`.
- **`after_track`** — GET-параметры: `{from, prevTrackId, nextTrackId, context, contextItem, types}`. Типы: `shot`, `ad`. Контексты: `playlist`.

---

## 6. Альбомы

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 24 | `albums()` | POST | `/albums` | Получение альбома/альбомов по ID |
| 25 | `albums_with_tracks()` | GET | `/albums/{album_id}/with-tracks` | Получение альбома вместе с треками |

**Детали:**

- **`albums`** — POST-данные: `{album-ids: ids}`.

---

## 7. Артисты (исполнители)

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 26 | `artists()` | POST | `/artists` | Получение исполнителей по ID |
| 27 | `artists_brief_info()` | GET | `/artists/{artist_id}/brief-info` | Подробная информация об артисте |
| 28 | `artists_tracks()` | GET | `/artists/{artist_id}/tracks` | Треки артиста (с пагинацией) |
| 29 | `artists_direct_albums()` | GET | `/artists/{artist_id}/direct-albums` | Альбомы артиста (с пагинацией и сортировкой) |

**Детали:**

- **`artists`** — POST-данные: `{artist-ids: ids}`.
- **`artists_tracks`** — GET-параметры: `{page: int, page-size: int}` (по умолч. page=0, page-size=20).
- **`artists_direct_albums`** — GET-параметры: `{sort-by: "year"|"rating", page: int, page-size: int}`.

---

## 8. Поиск

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 30 | `search()` | GET | `/search` | Поиск по запросу и типу |
| 31 | `search_suggest()` | GET | `/search/suggest` | Подсказки (автодополнение) для поиска |

**Детали:**

- **`search`** — GET-параметры: `{text: str, nocorrect: "True"/"False", type: str, page: int, playlist-in-best: "True"/"False"}`.
  - Типы: `all`, `artist`, `user`, `album`, `playlist`, `track`, `podcast`, `podcast_episode`.
  - При `type=all` подкасты и эпизоды не возвращаются.
- **`search_suggest`** — GET-параметры: `{part: str}`.

---

## 9. Плейлисты

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 32 | `playlists_list()` | POST | `/playlists/list` | Получение плейлистов по ID (формат `owner_id:playlist_id`) |
| 33 | `playlists_collective_join()` | POST | `/playlists/collective/join` | Присоединение к плейлисту как соавтор |
| 34 | `users_playlists()` (list) | POST | `/users/{user_id}/playlists` | Получение нескольких плейлистов пользователя (по kinds) |
| 35 | `users_playlists()` (single) | GET | `/users/{user_id}/playlists/{kind}` | Получение одного плейлиста пользователя |
| 36 | `users_playlists_list()` | GET | `/users/{user_id}/playlists/list` | Список всех плейлистов пользователя |
| 37 | `users_playlists_recommendations()` | GET | `/users/{user_id}/playlists/{kind}/recommendations` | Рекомендации для плейлиста |
| 38 | `users_playlists_create()` | POST | `/users/{user_id}/playlists/create` | Создание плейлиста |
| 39 | `users_playlists_delete()` | POST | `/users/{user_id}/playlists/{kind}/delete` | Удаление плейлиста |
| 40 | `users_playlists_name()` | POST | `/users/{user_id}/playlists/{kind}/name` | Изменение названия плейлиста |
| 41 | `users_playlists_visibility()` | POST | `/users/{user_id}/playlists/{kind}/visibility` | Изменение видимости плейлиста |
| 42 | `users_playlists_change()` | POST | `/users/{user_id}/playlists/{kind}/change` | Изменение содержимого плейлиста (diff) |

**Детали:**

- **`playlists_list`** — POST-данные: `{playlist-ids: ids}`. Формат ID: `owner_id:playlist_id`. Не возвращает треки.
- **`playlists_collective_join`** — POST-параметры: `{uid: int, token: str}`.
- **`users_playlists`** (list) — POST-данные: `{kinds: list}`.
- **`users_playlists_create`** — POST-данные: `{title: str, visibility: "public"|"private"}`.
- **`users_playlists_name`** — POST-данные: `{value: str}`.
- **`users_playlists_visibility`** — POST-данные: `{value: "private"|"public"}`.
- **`users_playlists_change`** — POST-данные: `{kind: int, revision: int, diff: str(JSON)}`.

---

## 10. Настройки пользователя

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 43 | `users_settings()` | GET | `/users/{user_id}/settings` | Получение настроек пользователя |

---

## 11. Лайки ("Мне нравится")

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 44 | `users_likes_tracks()` | GET | `/users/{user_id}/likes/tracks` | Получение треков с лайком |
| 45 | `users_likes_albums()` | GET | `/users/{user_id}/likes/albums` | Получение альбомов с лайком |
| 46 | `users_likes_artists()` | GET | `/users/{user_id}/likes/artists` | Получение артистов с лайком |
| 47 | `users_likes_playlists()` | GET | `/users/{user_id}/likes/playlists` | Получение плейлистов с лайком |
| 48 | `users_likes_tracks_add()` | POST | `/users/{user_id}/likes/tracks/add-multiple` | Добавить лайк трекам |
| 49 | `users_likes_tracks_remove()` | POST | `/users/{user_id}/likes/tracks/remove` | Удалить лайк у треков |
| 50 | `users_likes_artists_add()` | POST | `/users/{user_id}/likes/artists/add-multiple` | Добавить лайк артистам |
| 51 | `users_likes_artists_remove()` | POST | `/users/{user_id}/likes/artists/remove` | Удалить лайк у артистов |
| 52 | `users_likes_playlists_add()` | POST | `/users/{user_id}/likes/playlists/add-multiple` | Добавить лайк плейлистам |
| 53 | `users_likes_playlists_remove()` | POST | `/users/{user_id}/likes/playlists/remove` | Удалить лайк у плейлистов |
| 54 | `users_likes_albums_add()` | POST | `/users/{user_id}/likes/albums/add-multiple` | Добавить лайк альбомам |
| 55 | `users_likes_albums_remove()` | POST | `/users/{user_id}/likes/albums/remove` | Удалить лайк у альбомов |

**Детали:**

- **GET-запросы лайков** — параметры: `{if-modified-since-revision: int}` (для треков), `{rich: "True"}` (для альбомов), `{with-timestamps: "True"}` (для артистов).
- **POST-запросы лайков** — данные: `{track-ids: ids}`, `{artist-ids: ids}`, `{playlist-ids: ids}`, `{album-ids: ids}` соответственно.
- ID плейлиста в формате `owner_id:playlist_id`.

---

## 12. Дизлайки ("Не рекомендовать")

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 56 | `users_dislikes_tracks()` | GET | `/users/{user_id}/dislikes/tracks` | Получение треков с отметкой "Не рекомендовать" |
| 57 | `users_dislikes_tracks_add()` | POST | `/users/{user_id}/dislikes/tracks/add-multiple` | Добавить "Не рекомендовать" трекам |
| 58 | `users_dislikes_tracks_remove()` | POST | `/users/{user_id}/dislikes/tracks/remove` | Снять "Не рекомендовать" у треков |

**Детали:**

- **GET** — параметры: `{if_modified_since_revision: int}`.
- **POST** — данные: `{track-ids: ids}`.

---

## 13. Радио (Rotor)

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 59 | `rotor_account_status()` | GET | `/rotor/account/status` | Статус пользователя с доп. полями (skips_per_hour и т.д.) |
| 60 | `rotor_stations_dashboard()` | GET | `/rotor/stations/dashboard` | Рекомендованные радиостанции |
| 61 | `rotor_stations_list()` | GET | `/rotor/stations/list` | Все радиостанции с настройками пользователя |
| 62 | `rotor_station_info()` | GET | `/rotor/station/{station}/info` | Информация о конкретной станции |
| 63 | `rotor_station_tracks()` | GET | `/rotor/station/{station}/tracks` | Получение цепочки треков станции |
| 64 | `rotor_station_feedback()` | POST | `/rotor/station/{station}/feedback` | Отправка обратной связи (начало, конец, скип трека) |
| 65 | `rotor_station_settings2()` | POST | `/rotor/station/{station}/settings3` | Изменение настроек станции |

**Детали:**

- **`rotor_stations_list`** — GET-параметры: `{language: str}`. Языки: `en`, `uz`, `uk`, `us`, `ru`, `kk`, `hy`.
- **`rotor_station_tracks`** — GET-параметры: `{settings2: "True", queue: track_id}`. Формат станции: `<type>:<id>` (напр. `user:onyourwave`, `genre:allrock`, `track:1234`).
- **`rotor_station_feedback`** — POST-данные: `{type: str, timestamp: float, trackId: str, from: str, totalPlayedSeconds: float}`, GET-параметры: `{batch-id: str}`.
  - Типы: `radioStarted`, `trackStarted`, `trackFinished`, `skip`.
- **`rotor_station_settings2`** — POST-данные: `{moodEnergy: str, diversity: str, type: str, language: str}`.
  - `moodEnergy`: `fun`, `active`, `calm`, `sad`, `all`.
  - `diversity`: `favorite`, `popular`, `discover`, `default`.
  - `language`: `not-russian`, `russian`, `any`.
  - `type`: `rotor`, `generative`.

---

## 14. Очередь прослушивания

| # | Метод | HTTP | Эндпоинт | Описание |
|---|-------|------|----------|----------|
| 66 | `queues_list()` | GET | `/queues` | Список очередей со всех устройств |
| 67 | `queue()` | GET | `/queues/{queue_id}` | Информация об очереди и треках в ней |
| 68 | `queue_update_position()` | POST | `/queues/{queue_id}/update-position` | Установка текущего индекса трека в очереди |
| 69 | `queue_create()` | POST | `/queues` | Создание новой очереди треков |

**Детали:**

- Все запросы очереди используют заголовок `X-Yandex-Music-Device: {device}`.
- Формат `device`: `os=Android; os_version=11; manufacturer=Samsung; model=Galaxy; clid=; device_id={uuid}; uuid={uuid}`.
- **`queue_update_position`** — POST-данные: `{isInteractive: false}`, GET-параметры: `{currentIndex: int}`.
- **`queue_create`** — POST-тело: JSON объекта `Queue`.

---

## Сводка

| Категория | Эндпоинтов |
|-----------|------------|
| Аккаунт | 5 |
| Фид | 2 |
| Лендинг/Каталог | 5 |
| Жанры/Теги | 2 |
| Треки | 6 |
| Альбомы | 2 |
| Артисты | 4 |
| Поиск | 2 |
| Плейлисты | 10 |
| Настройки пользователя | 1 |
| Лайки | 12 |
| Дизлайки | 3 |
| Радио (Rotor) | 5 |
| Очередь | 3 |
| **Итого** | **~62** |

---

## Примеры запросов

### Аутентификация

API поддерживает два способа аутентификации:

1. **OAuth токен** (рекомендуется для приложений):
   ```bash
   -H "Authorization: OAuth y0_AgAAAA..."
   ```

2. **Cookie Session_id** (из браузера):
   ```bash
   -H "Cookie: Session_id=3:1234567890..."
   ```

**Как получить Session_id:**
1. Открыть https://music.yandex.ru/ и залогиниться
2. DevTools (F12) → Network → найти запрос к `api.music.yandex.net`
3. В Headers найти `Cookie: Session_id=...`

> **Важно:** В cookie символ `|` нужно экранировать как `\|` в bash.

---

### Проверенные примеры

#### 1. Статус аккаунта

```bash
curl -s "https://api.music.yandex.net/account/status" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

**Пример ответа:**
```json
{
  "result": {
    "account": {
      "now": "2026-01-28T15:12:36+03:00",
      "region": 225,
      "regionCode": "ru",
      "serviceAvailable": true
    },
    "permissions": {
      "values": ["landing-play", "feed-play", "mix-play"]
    }
  }
}
```

---

#### 2. Поиск артиста

```bash
curl -s "https://api.music.yandex.net/search?text=Queen&type=artist&page=0" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

**Пример ответа (сокращённо):**
```json
{
  "result": {
    "artists": {
      "results": [
        {
          "id": 79215,
          "name": "Queen",
          "genres": ["rock", "hardrock", "films"]
        }
      ]
    }
  }
}
```

---

#### 3. Информация об артисте

```bash
curl -s "https://api.music.yandex.net/artists/79215/brief-info" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

**Пример ответа (сокращённо):**
```json
{
  "result": {
    "artist": {
      "id": 79215,
      "name": "Queen"
    },
    "stats": {
      "lastMonthListeners": 3283937
    },
    "popularTracks": [
      {"id": "2758009", "title": "The Show Must Go On"},
      {"id": "1710811", "title": "We Will Rock You"},
      {"id": "44092998", "title": "I Want To Break Free"}
    ]
  }
}
```

---

#### 4. Чарт (ТОП-100)

```bash
curl -s "https://api.music.yandex.net/landing3/chart" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

**Пример ответа (сокращённо):**
```json
{
  "result": {
    "chart": {
      "title": "Чарт",
      "tracks": [
        {
          "track": {"title": "БАНК", "artists": [{"name": "ICEGERGERT"}]},
          "chart": {"position": 1}
        },
        {
          "track": {"title": "Жиганская", "artists": [{"name": "Jakone"}]},
          "chart": {"position": 2}
        }
      ]
    }
  }
}
```

---

#### 5. Фид (рекомендации)

```bash
curl -s "https://api.music.yandex.net/feed" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

---

#### 6. Поиск треков

```bash
curl -s "https://api.music.yandex.net/search?text=Bohemian%20Rhapsody&type=track" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

---

#### 7. Получение трека по ID (POST)

```bash
curl -s "https://api.music.yandex.net/tracks" \
  -X POST \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "track-ids=2758009"
```

---

#### 8. Жанры музыки

```bash
curl -s "https://api.music.yandex.net/genres" \
  -H "Cookie: Session_id=YOUR_SESSION_ID" \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

---

## Скачивание треков

### Алгоритм получения прямой ссылки на MP3

Скачивание трека требует 3 шага:

#### Шаг 1: Получить download-info

```bash
curl -s "https://api.music.yandex.net/tracks/{track_id}/download-info" \
  -H "Cookie: Session_id=..." \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

**Ответ:**
```json
{
  "result": [{
    "codec": "mp3",
    "bitrateInKbps": 128,
    "downloadInfoUrl": "https://api.music.yandex.net/get-mp3/...",
    "direct": false
  }]
}
```

#### Шаг 2: Получить XML с параметрами

```bash
curl -s "{downloadInfoUrl}"
```

**Ответ (XML):**
```xml
<download-info>
  <host>api.music.yandex.net</host>
  <path>/U2FsdGVkX19...</path>
  <ts>19c04d4eff8</ts>
  <s>JmlkaMgw6nyc...</s>
</download-info>
```

#### Шаг 3: Построить прямую ссылку

```kotlin
val SIGN_SALT = "XGRlBW9FXlekgbPrRHuSiA"

// path.substring(1) — без первого символа "/"
val sign = md5(SIGN_SALT + path.substring(1) + s)

val directLink = "https://$host/get-mp3/$sign/$ts$path"
```

#### Шаг 4: Скачать файл

```bash
curl -s -L "{direct_link}" -o track.mp3
```

**Важно:**
- Ссылка действительна **~1 минуту** после получения download-info
- Доступные битрейты: 64, 128, 192, 320 kbps (зависит от подписки)
- Без подписки доступен только preview (30 сек) в 128 kbps

---

## Подпись запросов (для lyrics)

Некоторые эндпоинты требуют подписи запроса (например, `/tracks/{id}/lyrics`).

### Алгоритм подписи

```kotlin

const val SIGN_KEY = "p93jhgh689SBReK6ghtw62"

fun signRequest(trackId: Long): Pair<Long, String> {
    val timestamp = System.currentTimeMillis() / 1000
    val message = "$trackId$timestamp"

    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(SIGN_KEY.toByteArray(), "HmacSHA256"))
    val hmacSign = mac.doFinal(message.toByteArray())

    val sign = Base64.encodeToString(hmacSign, Base64.NO_WRAP)
    return Pair(timestamp, sign)
}
```

### Пример запроса lyrics

```bash
# timestamp и sign вычисляются по алгоритму выше
curl -s "https://api.music.yandex.net/tracks/2758009/lyrics?format=TEXT&timeStamp={timestamp}&sign={sign}" \
  -H "Cookie: Session_id=..." \
  -H "X-Yandex-Music-Client: YandexMusicAndroid/24023621"
```

---

## Модели данных

### Track (Трек)

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | int/str | Уникальный идентификатор |
| `title` | str | Название трека |
| `available` | bool | Доступен ли для прослушивания |
| `artists` | Artist[] | Список исполнителей |
| `albums` | Album[] | Список альбомов |
| `duration_ms` | int | Длительность в миллисекундах |
| `cover_uri` | str | Ссылка на обложку (шаблон) |
| `og_image` | str | Open Graph изображение |
| `lyrics_available` | bool | Есть ли текст песни |
| `explicit` | bool | Ненормативная лексика |
| `available_for_premium_users` | bool | Только для подписчиков |
| `remember_position` | bool | Запоминать позицию (для подкастов) |
| `content_warning` | str | Тип контента (`explicit`) |

### Album (Альбом)

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | int | Уникальный идентификатор |
| `title` | str | Название альбома |
| `track_count` | int | Количество треков |
| `artists` | Artist[] | Исполнители |
| `cover_uri` | str | Ссылка на обложку |
| `year` | int | Год релиза |
| `release_date` | str | Дата релиза (ISO 8601) |
| `genre` | str | Жанр |
| `type` | str | Тип (`single`, `compilation`) |
| `volumes` | Track[][] | Треки по дискам |
| `available` | bool | Доступен ли |

### Artist (Исполнитель)

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | int | Уникальный идентификатор |
| `name` | str | Имя исполнителя |
| `cover` | Cover | Обложка |
| `genres` | str[] | Жанры |
| `og_image` | str | Open Graph изображение |
| `counts` | Counts | Счётчики (треки, альбомы) |
| `popular_tracks` | Track[] | Популярные треки |
| `likes_count` | int | Количество лайков |

### Playlist (Плейлист)

| Поле | Тип | Описание |
|------|-----|----------|
| `kind` | int | ID плейлиста |
| `uid` | int | ID владельца |
| `title` | str | Название |
| `track_count` | int | Количество треков |
| `cover` | Cover | Обложка |
| `tracks` | TrackShort[] | Треки (сокращённо) |
| `visibility` | str | Видимость (`public`/`private`) |
| `revision` | int | Версия (для изменений) |

### Обложки (cover_uri)

Поле `cover_uri` содержит шаблон ссылки. Замените `%%` на размер:

```
https://avatars.yandex.net/get-music-content/.../{size}x{size}
```

**Доступные размеры:** 30, 50, 80, 100, 200, 300, 400, 600, 800, 1000

**Пример:**
```
// Шаблон
avatars.yandex.net/get-music-content/123/abc-%%

// Результат (200x200)
https://avatars.yandex.net/get-music-content/123/abc-200x200
```

---

## Обработка ошибок

### HTTP коды

| Код | Описание |
|-----|----------|
| 400 | Неверный запрос |
| 401 | Не авторизован |
| 403 | Нет прав доступа |
| 404 | Ресурс не найден |
| 409 | Конфликт |
| 410 | Ссылка истекла (download) |
| 413 | Слишком большой запрос |
| 502 | Bad Gateway |

### Формат ошибки API

```json
{
  "error": "not-found",
  "error_description": "Track not found"
}
```

---

## Заголовки запросов

### Обязательные

| Заголовок | Значение | Описание |
|-----------|----------|----------|
| `X-Yandex-Music-Client` | `YandexMusicAndroid/24023621` | Идентификатор клиента |

### Аутентификация (один из)

| Заголовок | Формат | Описание |
|-----------|--------|----------|
| `Authorization` | `OAuth {token}` | OAuth токен |
| `Cookie` | `Session_id={session}` | Сессия из браузера |

### Опциональные

| Заголовок | Значение | Описание |
|-----------|----------|----------|
| `Accept-Language` | `ru`, `en`, и др. | Язык ответов |
| `X-Yandex-Music-Device` | см. ниже | Для работы с очередью |

**Формат X-Yandex-Music-Device:**
```
os=Android; os_version=11; manufacturer=Samsung; model=Galaxy S21;
clid=; device_id={uuid}; uuid={uuid}
```

---

## Ограничения и подписка

### Проверка подписки

Эндпоинт `/account/status` возвращает информацию о подписке:

```json
{
  "result": {
    "account": { "uid": 123456 },
    "permissions": {
      "values": ["landing-play", "feed-play", "mix-play"]
    },
    "plus": {
      "hasPlus": true,
      "isTutorialCompleted": true
    },
    "subscription": {
      "autoRenewable": [...],
      "canStartTrial": false
    }
  }
}
```

### Права доступа (permissions)

| Право | Описание |
|-------|----------|
| `landing-play` | Воспроизведение с главной |
| `feed-play` | Воспроизведение из фида |
| `mix-play` | Воспроизведение миксов |
| `radio-play` | Радио |
| `play-premium` | Полное прослушивание (подписка) |

---

## Рекомендации для Android

### 1. Аутентификация

- Используйте OAuth токен, не Session_id
- Храните токен в EncryptedSharedPreferences
- Обновляйте токен при 401 ошибке

### 2. Кэширование

- Кэшируйте метаданные треков/альбомов
- Обложки кэшируйте через Coil/Glide
- Download-info НЕ кэшировать (истекает за 1 мин)

### 3. Воспроизведение

- Используйте ExoPlayer с поддержкой MP3
- Отправляйте play_audio для статистики
- Для радио: rotor_station_feedback

### 4. Оффлайн режим

- Скачивайте треки в Internal Storage
- Храните метаданные в Room/SQLite
- Проверяйте права перед скачиванием

### 5. Rate Limiting

- API не имеет строгих лимитов
- Рекомендуется: не более 10 req/sec
- Используйте batch-запросы где возможно

### 6. Полезные batch-эндпоинты

```bash
# Получить несколько треков за раз
POST /tracks
{"track-ids": "123,456,789"}

# Получить несколько альбомов
POST /albums
{"album-ids": "123,456"}

# Получить несколько артистов
POST /artists
{"artist-ids": "123,456"}
```
