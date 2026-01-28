package com.example.yandexmusic.core.data

import com.example.yandexmusic.core.network.ApiClient
import com.example.yandexmusic.core.network.ArtistInfo
import com.example.yandexmusic.core.network.ChartResult
import com.example.yandexmusic.core.network.SearchResult
import com.example.yandexmusic.core.network.dto.feed.FeedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(private val token: String) {

    private val api = ApiClient.api
    private val authHeader = "OAuth $token"

    suspend fun search(query: String, type: String = "all"): Result<SearchResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.search(authHeader, query, type)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getChart(): Result<ChartResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getChart(authHeader)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getFeed(): Result<FeedResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getFeed(authHeader)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getArtistInfo(artistId: Long): Result<ArtistInfo> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getArtistInfo(authHeader, artistId)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
