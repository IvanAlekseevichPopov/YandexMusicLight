package com.example.yandexmusic.core.network.dto.feed

import com.google.gson.annotations.SerializedName

/**
 * Полный ответ API /feed
 * Содержит умные плейлисты, заголовки и данные по дням
 */
data class FeedResponse(
    /** Флаг возможности воспроизведения */
    @SerializedName("canGetMoreEvents")
    val canGetMoreEvents: Boolean? = null,

    /** Пейджинг токен для загрузки следующей страницы */
    @SerializedName("pumpkin")
    val pumpkin: Boolean? = null,

    /** Сгенерированные (умные) плейлисты: Плейлист дня, Дежавю, Премьера и т.д. */
    @SerializedName("generatedPlaylists")
    val generatedPlaylists: List<GeneratedPlaylist>? = null,

    /** Заголовки блоков фида */
    @SerializedName("headlines")
    val headlines: List<Headline>? = null,

    /** Данные по дням (рекомендации, события) */
    @SerializedName("days")
    val days: List<Day>? = null,

    /** Сегодняшний день в формате ISO */
    @SerializedName("today")
    val today: String? = null,

    /** Флаг наличия новых релизов */
    @SerializedName("isWizardPassed")
    val isWizardPassed: Boolean? = null
)

/**
 * Сгенерированный (умный) плейлист
 * Типы: playlistOfTheDay, origin, dejavu, neverHeard, recentTracks, missedLikes, kinopoisk
 */
data class GeneratedPlaylist(
    /** Тип плейлиста */
    @SerializedName("type")
    val type: String? = null,

    /** Готовность плейлиста */
    @SerializedName("ready")
    val ready: Boolean? = null,

    /** Флаг уведомления */
    @SerializedName("notify")
    val notify: Boolean? = null,

    /** Данные плейлиста */
    @SerializedName("data")
    val data: GeneratedPlaylistData? = null,

    /** Описание плейлиста */
    @SerializedName("description")
    val description: List<DescriptionPart>? = null
)

/**
 * Данные сгенерированного плейлиста
 */
data class GeneratedPlaylistData(
    /** ID плейлиста (kind) */
    @SerializedName("kind")
    val kind: Int? = null,

    /** ID владельца плейлиста */
    @SerializedName("uid")
    val uid: Long? = null,

    /** Название плейлиста */
    @SerializedName("title")
    val title: String? = null,

    /** Описание плейлиста */
    @SerializedName("description")
    val description: String? = null,

    /** Количество треков */
    @SerializedName("trackCount")
    val trackCount: Int? = null,

    /** Теги плейлиста */
    @SerializedName("tags")
    val tags: List<Tag>? = null,

    /** Обложка плейлиста */
    @SerializedName("cover")
    val cover: Cover? = null,

    /** URI обложки (шаблон с %% для размера) */
    @SerializedName("ogImage")
    val ogImage: String? = null,

    /** Владелец плейлиста */
    @SerializedName("owner")
    val owner: Owner? = null,

    /** Треки плейлиста (краткая информация) */
    @SerializedName("tracks")
    val tracks: List<TrackShort>? = null,

    /** Видимость плейлиста */
    @SerializedName("visibility")
    val visibility: String? = null,

    /** Дата последнего изменения */
    @SerializedName("modified")
    val modified: String? = null,

    /** Ревизия (версия) плейлиста */
    @SerializedName("revision")
    val revision: Int? = null,

    /** Флаг наличия фонового изображения */
    @SerializedName("backgroundImageUrl")
    val backgroundImageUrl: String? = null,

    /** Флаг наличия анимированной обложки */
    @SerializedName("animatedCoverUri")
    val animatedCoverUri: String? = null,

    /** Флаг "умного" плейлиста */
    @SerializedName("isBanner")
    val isBanner: Boolean? = null,

    /** Флаг премиум-контента */
    @SerializedName("isPremiere")
    val isPremiere: Boolean? = null,

    /** Длительность плейлиста в миллисекундах */
    @SerializedName("durationMs")
    val durationMs: Long? = null,

    /** ID коллекции обложек */
    @SerializedName("collective")
    val collective: Boolean? = null,

    /** Дата обновления плейлиста */
    @SerializedName("playlistUuid")
    val playlistUuid: String? = null,

    /** Тип плейлиста */
    @SerializedName("generatedPlaylistType")
    val generatedPlaylistType: String? = null,

    /** Флаг доступности для похожих плейлистов */
    @SerializedName("idForFrom")
    val idForFrom: String? = null
)

/**
 * Часть описания плейлиста (для rich text)
 */
data class DescriptionPart(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("type")
    val type: String? = null
)

/**
 * Заголовок блока фида
 */
data class Headline(
    /** Тип заголовка */
    @SerializedName("type")
    val type: String? = null,

    /** ID заголовка */
    @SerializedName("id")
    val id: String? = null,

    /** Текст заголовка */
    @SerializedName("message")
    val message: String? = null
)

/**
 * День в фиде (содержит события и рекомендации за конкретный день)
 */
data class Day(
    /** Дата дня в формате ISO */
    @SerializedName("day")
    val day: String? = null,

    /** События дня */
    @SerializedName("events")
    val events: List<Event>? = null,

    /** Количество треков */
    @SerializedName("tracksToPlay")
    val tracksToPlay: List<FeedTrack>? = null,

    /** Треки для отображения */
    @SerializedName("tracksToPlayWithAds")
    val tracksToPlayWithAds: List<FeedTrack>? = null
)

/**
 * Событие в фиде
 */
data class Event(
    /** ID события */
    @SerializedName("id")
    val id: String? = null,

    /** Тип события: tracks, artists, albums, notification */
    @SerializedName("type")
    val type: String? = null,

    /** Тип для отображения (может отличаться от type) */
    @SerializedName("typeForFrom")
    val typeForFrom: String? = null,

    /** Заголовок события (может быть String или List) */
    @SerializedName("title")
    val title: Any? = null,

    /** Треки события (для type=tracks) */
    @SerializedName("tracks")
    val tracks: List<FeedTrack>? = null,

    /** Артисты события (для type=artists) */
    @SerializedName("artists")
    val artists: List<FeedArtist>? = null,

    /** Альбомы события (для type=albums) */
    @SerializedName("albums")
    val albums: List<FeedAlbum>? = null,

    /** Сообщение события (для type=notification) */
    @SerializedName("message")
    val message: String? = null,

    /** Устройство события */
    @SerializedName("device")
    val device: String? = null,

    /** Жанр события */
    @SerializedName("genre")
    val genre: String? = null,

    /** Похожие треки */
    @SerializedName("similarToTrack")
    val similarToTrack: FeedTrack? = null
)

/**
 * Часть заголовка события (rich text)
 */
data class TitlePart(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("type")
    val type: String? = null
)

/**
 * Трек в фиде
 */
data class FeedTrack(
    /** ID трека */
    @SerializedName("id")
    val id: String? = null,

    /** Название трека */
    @SerializedName("title")
    val title: String? = null,

    /** Доступность трека */
    @SerializedName("available")
    val available: Boolean? = null,

    /** Доступность для премиум-пользователей */
    @SerializedName("availableForPremiumUsers")
    val availableForPremiumUsers: Boolean? = null,

    /** Доступность полной версии */
    @SerializedName("availableFullWithoutPermission")
    val availableFullWithoutPermission: Boolean? = null,

    /** Длительность в миллисекундах */
    @SerializedName("durationMs")
    val durationMs: Long? = null,

    /** URI обложки */
    @SerializedName("coverUri")
    val coverUri: String? = null,

    /** Исполнители трека */
    @SerializedName("artists")
    val artists: List<FeedArtist>? = null,

    /** Альбомы трека */
    @SerializedName("albums")
    val albums: List<FeedAlbum>? = null,

    /** Наличие текста */
    @SerializedName("lyricsAvailable")
    val lyricsAvailable: Boolean? = null,

    /** Наличие ненормативной лексики */
    @SerializedName("explicit")
    val explicit: Boolean? = null,

    /** Реальный ID трека */
    @SerializedName("realId")
    val realId: String? = null,

    /** OG изображение */
    @SerializedName("ogImage")
    val ogImage: String? = null,

    /** Тип контента */
    @SerializedName("type")
    val type: String? = null,

    /** Предупреждение о контенте */
    @SerializedName("contentWarning")
    val contentWarning: String? = null,

    /** Флаг "запомнить позицию" (для подкастов) */
    @SerializedName("rememberPosition")
    val rememberPosition: Boolean? = null,

    /** Контекст в плейлисте (chart позиция и т.п.) */
    @SerializedName("chart")
    val chart: ChartInfo? = null
)

/**
 * Информация о позиции в чарте
 */
data class ChartInfo(
    @SerializedName("position")
    val position: Int? = null,

    @SerializedName("progress")
    val progress: String? = null,

    @SerializedName("listeners")
    val listeners: Int? = null,

    @SerializedName("shift")
    val shift: Int? = null
)

/**
 * Артист в фиде
 */
data class FeedArtist(
    /** ID артиста */
    @SerializedName("id")
    val id: Long? = null,

    /** Имя артиста */
    @SerializedName("name")
    val name: String? = null,

    /** URI обложки */
    @SerializedName("cover")
    val cover: Cover? = null,

    /** Флаг "различные артисты" */
    @SerializedName("various")
    val various: Boolean? = null,

    /** Жанры */
    @SerializedName("genres")
    val genres: List<String>? = null,

    /** OG изображение */
    @SerializedName("ogImage")
    val ogImage: String? = null,

    /** Количество лайков */
    @SerializedName("likesCount")
    val likesCount: Int? = null,

    /** Флаг композитора */
    @SerializedName("composer")
    val composer: Boolean? = null,

    /** Счетчики */
    @SerializedName("counts")
    val counts: ArtistCounts? = null
)

/**
 * Счетчики артиста
 */
data class ArtistCounts(
    @SerializedName("tracks")
    val tracks: Int? = null,

    @SerializedName("directAlbums")
    val directAlbums: Int? = null,

    @SerializedName("alsoAlbums")
    val alsoAlbums: Int? = null,

    @SerializedName("alsoTracks")
    val alsoTracks: Int? = null
)

/**
 * Альбом в фиде
 */
data class FeedAlbum(
    /** ID альбома */
    @SerializedName("id")
    val id: Long? = null,

    /** Название альбома */
    @SerializedName("title")
    val title: String? = null,

    /** Тип альбома (single, compilation и т.д.) */
    @SerializedName("type")
    val type: String? = null,

    /** Тип мета данных */
    @SerializedName("metaType")
    val metaType: String? = null,

    /** Год выпуска */
    @SerializedName("year")
    val year: Int? = null,

    /** Дата релиза */
    @SerializedName("releaseDate")
    val releaseDate: String? = null,

    /** URI обложки */
    @SerializedName("coverUri")
    val coverUri: String? = null,

    /** OG изображение */
    @SerializedName("ogImage")
    val ogImage: String? = null,

    /** Жанр */
    @SerializedName("genre")
    val genre: String? = null,

    /** Количество треков */
    @SerializedName("trackCount")
    val trackCount: Int? = null,

    /** Количество лайков */
    @SerializedName("likesCount")
    val likesCount: Int? = null,

    /** Флаг наличия новинок */
    @SerializedName("recent")
    val recent: Boolean? = null,

    /** Флаг релиза */
    @SerializedName("veryImportant")
    val veryImportant: Boolean? = null,

    /** Артисты альбома */
    @SerializedName("artists")
    val artists: List<FeedArtist>? = null,

    /** Лейблы */
    @SerializedName("labels")
    val labels: List<Label>? = null,

    /** Доступность */
    @SerializedName("available")
    val available: Boolean? = null,

    /** Доступность для премиум-пользователей */
    @SerializedName("availableForPremiumUsers")
    val availableForPremiumUsers: Boolean? = null,

    /** Доступность для мобильных устройств */
    @SerializedName("availableForMobile")
    val availableForMobile: Boolean? = null,

    /** Доступен ли частично */
    @SerializedName("availablePartially")
    val availablePartially: Boolean? = null,

    /** Количество дисков */
    @SerializedName("bests")
    val bests: List<Long>? = null,

    /** Длительность в миллисекундах */
    @SerializedName("durationMs")
    val durationMs: Long? = null,

    /** Explicit */
    @SerializedName("explicit")
    val explicit: Boolean? = null,

    /** Предупреждение о контенте */
    @SerializedName("contentWarning")
    val contentWarning: String? = null
)

/**
 * Лейбл
 */
data class Label(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("name")
    val name: String? = null
)

/**
 * Обложка
 */
data class Cover(
    /** Тип обложки */
    @SerializedName("type")
    val type: String? = null,

    /** URI обложки (шаблон с %% для размера) */
    @SerializedName("uri")
    val uri: String? = null,

    /** Ссылки для составных обложек (мозаика) */
    @SerializedName("itemsUri")
    val itemsUri: List<String>? = null,

    /** Пользовательская обложка */
    @SerializedName("custom")
    val custom: Boolean? = null,

    /** Префикс для uri */
    @SerializedName("prefix")
    val prefix: String? = null,

    /** Цвет фона */
    @SerializedName("bgColor")
    val bgColor: String? = null,

    /** Цвет текста */
    @SerializedName("textColor")
    val textColor: String? = null
)

/**
 * Владелец плейлиста
 */
data class Owner(
    /** ID пользователя */
    @SerializedName("uid")
    val uid: Long? = null,

    /** Логин */
    @SerializedName("login")
    val login: String? = null,

    /** Имя */
    @SerializedName("name")
    val name: String? = null,

    /** Флаг верификации */
    @SerializedName("verified")
    val verified: Boolean? = null
)

/**
 * Тег плейлиста
 */
data class Tag(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("value")
    val value: String? = null
)

/**
 * Краткая информация о треке в плейлисте
 */
data class TrackShort(
    /** ID трека */
    @SerializedName("id")
    val id: Long? = null,

    /** ID альбома */
    @SerializedName("albumId")
    val albumId: Long? = null,

    /** Временная метка добавления */
    @SerializedName("timestamp")
    val timestamp: String? = null,

    /** Полные данные трека (если запрошены) */
    @SerializedName("track")
    val track: FeedTrack? = null
)
