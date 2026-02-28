package com.example.yandexmusic.core.data

import android.util.Log
import com.example.yandexmusic.core.network.ApiClient
import com.example.yandexmusic.core.network.ArtistInfo
import com.example.yandexmusic.core.network.ChartResult
import com.example.yandexmusic.core.network.SearchResult
import com.example.yandexmusic.core.network.dto.feed.FeedResponse
import com.example.yandexmusic.core.network.dto.track.DownloadInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository {

    companion object {
        private const val TAG = "MusicRepository"
    }

    private val api = ApiClient.api

    suspend fun search(query: String, type: String = "all"): Result<SearchResult> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "search: query=$query, type=$type")
            try {
                val response = api.search(query, type)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Log.e(TAG, "search error", e)
                Result.failure(e)
            }
        }

    suspend fun getChart(): Result<ChartResult> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getChart")
            try {
                val response = api.getChart()
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Log.e(TAG, "getChart error", e)
                Result.failure(e)
            }
        }

    suspend fun getFeed(): Result<FeedResponse> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getFeed")
            try {
                val response = api.getFeed()
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Log.e(TAG, "getFeed error", e)
                Result.failure(e)
            }
        }

    suspend fun getArtistInfo(artistId: Long): Result<ArtistInfo> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getArtistInfo: artistId=$artistId")
            try {
                val response = api.getArtistInfo(artistId)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } catch (e: Exception) {
                Log.e(TAG, "getArtistInfo error", e)
                Result.failure(e)
            }
        }

    suspend fun getDownloadInfo(trackId: Long): Result<List<DownloadInfo>> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getDownloadInfo: trackId=$trackId")
            try {
                val response = api.getDownloadInfo(trackId)
                response.result?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty download info"))
            } catch (e: Exception) {
                Log.e(TAG, "getDownloadInfo error", e)
                Result.failure(e)
            }
        }

    suspend fun getDownloadInfoXml(url: String): Result<String> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getDownloadInfoXml: url=$url")
            try {
                val response = api.getDownloadInfoXml(url)
                val xml = response.string()
                Result.success(xml)
            } catch (e: Exception) {
                Log.e(TAG, "getDownloadInfoXml error", e)
                Result.failure(e)
            }
        }
}
