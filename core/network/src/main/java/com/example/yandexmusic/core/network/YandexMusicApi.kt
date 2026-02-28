package com.example.yandexmusic.core.network

import com.example.yandexmusic.core.network.dto.feed.FeedResponse
import com.example.yandexmusic.core.network.dto.track.DownloadInfo
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Yandex Music API интерфейс
 * Base URL: https://api.music.yandex.ru
 */
interface YandexMusicApi {

    @GET("account/status")
    suspend fun getAccountStatus(): ApiResponse<AccountStatus>

    @GET("search")
    suspend fun search(
        @Query("text") query: String,
        @Query("type") type: String = "all",
        @Query("page") page: Int = 0
    ): ApiResponse<SearchResult>

    @GET("landing3/chart")
    suspend fun getChart(): ApiResponse<ChartResult>

    @GET("feed")
    suspend fun getFeed(): ApiResponse<FeedResponse>

    @GET("artists/{artistId}/brief-info")
    suspend fun getArtistInfo(
        @Path("artistId") artistId: Long
    ): ApiResponse<ArtistInfo>

    // === Download ===

    /**
     * Шаг 1: Получить информацию о вариантах скачивания трека
     */
    @GET("tracks/{trackId}/download-info")
    suspend fun getDownloadInfo(
        @Path("trackId") trackId: Long
    ): ApiResponse<List<DownloadInfo>>

    /**
     * Шаг 2: Получить XML с параметрами для построения прямой ссылки
     * @param url полный URL из downloadInfoUrl
     */
    @GET
    suspend fun getDownloadInfoXml(
        @Url url: String
    ): ResponseBody
}

// === Общие модели ответов API ===

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
