package com.example.yandexmusic.core.network

import com.example.yandexmusic.core.network.dto.feed.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Yandex Music API интерфейс
 * Base URL: https://api.music.yandex.net
 */
interface YandexMusicApi {

    @GET("account/status")
    suspend fun getAccountStatus(
        @Header("Authorization") token: String
    ): ApiResponse<AccountStatus>

    @GET("search")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("text") query: String,
        @Query("type") type: String = "all",
        @Query("page") page: Int = 0
    ): ApiResponse<SearchResult>

    @GET("landing3/chart")
    suspend fun getChart(
        @Header("Authorization") token: String
    ): ApiResponse<ChartResult>

    /**
     * Получение фида рекомендаций
     *
     * Фид содержит:
     * - generatedPlaylists: умные плейлисты (Плейлист дня, Дежавю, Премьера и т.д.)
     * - headlines: заголовки блоков
     * - days: данные по дням с событиями и рекомендациями
     *
     * @param token OAuth токен в формате "OAuth {token}"
     * @return FeedResponse с полной структурой фида
     */
    @GET("feed")
    suspend fun getFeed(
        @Header("Cookie") token: String
    ): ApiResponse<FeedResponse>

    @GET("artists/{artistId}/brief-info")
    suspend fun getArtistInfo(
        @Header("Authorization") token: String,
        @Path("artistId") artistId: Long
    ): ApiResponse<ArtistInfo>

    companion object {
        const val BASE_URL = "https://api.music.yandex.net/"
        const val CLIENT_HEADER = "YandexMusicAndroid/24023621"
    }
}

// === Общие модели ответов API ===

/** Обёртка ответа API */
data class ApiResponse<T>(val result: T?)

// === Модели для Account ===

data class AccountStatus(val account: Account?)
data class Account(val uid: Long?, val region: Int?)

// === Модели для Search ===

data class SearchResult(val tracks: TracksResult?, val artists: ArtistsResult?)
data class TracksResult(val results: List<Track>?)
data class ArtistsResult(val results: List<Artist>?)
data class Track(val id: Long, val title: String, val artists: List<Artist>?)
data class Artist(val id: Long, val name: String)

// === Модели для Chart ===

data class ChartResult(val chart: Chart?)
data class Chart(val title: String?, val tracks: List<ChartTrack>?)
data class ChartTrack(val track: Track?, val chart: ChartPosition?)
data class ChartPosition(val position: Int)

// === Модели для Artist Info ===

data class ArtistInfo(val artist: Artist?, val popularTracks: List<Track>?)
