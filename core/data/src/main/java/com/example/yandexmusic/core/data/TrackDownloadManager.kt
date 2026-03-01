package com.example.yandexmusic.core.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.yandexmusic.core.data.db.DownloadedTrackDao
import com.example.yandexmusic.core.data.db.DownloadedTrackEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException

data class CachedTrack(
    val uri: String,
    val codec: String,
    val bitrateInKbps: Int
)

/**
 * Управление скачиванием и кэшированием треков.
 *
 * Два хранилища:
 * - Preload cache: cacheDir/tracks/ — временный, ОС может удалить при нехватке места
 * - Permanent downloads: filesDir/downloads/ — постоянный, метаданные в Room
 */
class TrackDownloadManager(
    context: Context,
    private val playerService: PlayerService,
    private val dao: DownloadedTrackDao,
    private val httpClient: OkHttpClient,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "TrackDownloadManager"
        private const val PRELOAD_DIR = "tracks"
        private const val DOWNLOAD_DIR = "downloads"
        private const val BUFFER_SIZE = 8192
    }

    private val preloadDir = File(context.cacheDir, PRELOAD_DIR).apply { mkdirs() }
    private val downloadDir = File(context.filesDir, DOWNLOAD_DIR).apply { mkdirs() }

    init {
        scope.launch(Dispatchers.IO) { evictPreloadCache() }
    }

    // --- Cache checks ---

    /** Проверяет permanent downloads → preload cache. Возвращает CachedTrack или null. */
    suspend fun getCachedTrack(trackId: Long): CachedTrack? = withContext(Dispatchers.IO) {
        val downloadFile = File(downloadDir, "$trackId.mp3")
        if (downloadFile.exists() && downloadFile.length() > 0) {
            val entity = dao.getByTrackId(trackId)
            return@withContext CachedTrack(
                uri = Uri.fromFile(downloadFile).toString(),
                codec = entity?.codec ?: "mp3",
                bitrateInKbps = entity?.bitrateInKbps ?: 0
            )
        }

        val preloadFile = File(preloadDir, "$trackId.mp3")
        if (preloadFile.exists() && preloadFile.length() > 0) {
            return@withContext CachedTrack(
                uri = Uri.fromFile(preloadFile).toString(),
                codec = "mp3",
                bitrateInKbps = 0
            )
        }

        null
    }

    // --- Preload (временный кэш) ---

    /** Скачивает трек в preload cache. Если уже есть — возвращает существующий файл. */
    suspend fun preloadTrack(trackId: Long): Result<File> = withContext(Dispatchers.IO) {
        // Уже скачан permanently?
        val downloadFile = File(downloadDir, "$trackId.mp3")
        if (downloadFile.exists() && downloadFile.length() > 0) {
            Log.d(TAG, "preloadTrack $trackId: already downloaded permanently")
            return@withContext Result.success(downloadFile)
        }

        // Уже в preload cache?
        val preloadFile = File(preloadDir, "$trackId.mp3")
        if (preloadFile.exists() && preloadFile.length() > 0) {
            Log.d(TAG, "preloadTrack $trackId: already preloaded")
            return@withContext Result.success(preloadFile)
        }

        Log.d(TAG, "preloadTrack $trackId: starting download to cache")
        downloadToFile(trackId, preloadFile)
    }

    // --- Permanent downloads ---

    /** Скачивает трек в постоянное хранилище + сохраняет метаданные в Room. */
    suspend fun downloadTrack(
        trackId: Long,
        title: String,
        artistName: String?
    ): Result<DownloadedTrackEntity> = withContext(Dispatchers.IO) {
        // Уже в Room?
        dao.getByTrackId(trackId)?.let {
            Log.d(TAG, "downloadTrack $trackId: already downloaded")
            return@withContext Result.success(it)
        }

        val targetFile = File(downloadDir, "$trackId.mp3")

        // Если есть в preload cache — переместить
        val preloadFile = File(preloadDir, "$trackId.mp3")
        if (preloadFile.exists() && preloadFile.length() > 0) {
            Log.d(TAG, "downloadTrack $trackId: moving from preload cache")
            preloadFile.copyTo(targetFile, overwrite = true)
            preloadFile.delete()
        } else {
            // Скачать заново
            val result = downloadToFile(trackId, targetFile)
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull()!!)
            }
        }

        val entity = DownloadedTrackEntity(
            trackId = trackId,
            title = title,
            artistName = artistName,
            codec = "mp3",
            bitrateInKbps = 192,
            fileName = "$trackId.mp3",
            fileSizeBytes = targetFile.length(),
            downloadedAt = System.currentTimeMillis()
        )
        dao.insert(entity)
        Log.d(TAG, "downloadTrack $trackId: saved, size=${targetFile.length()}")
        Result.success(entity)
    }

    /** Удаляет скачанный трек (файл + Room). */
    suspend fun removeDownload(trackId: Long) = withContext(Dispatchers.IO) {
        File(downloadDir, "$trackId.mp3").delete()
        dao.deleteByTrackId(trackId)
        Log.d(TAG, "removeDownload $trackId: done")
    }

    /** Flow списка скачанных треков для Library экрана. */
    fun getDownloadedTracks(): Flow<List<DownloadedTrackEntity>> = dao.getAllDownloaded()

    // --- Maintenance ---

    /** Удаляет preload-файлы старше maxAgeMs. */
    private fun evictPreloadCache(maxAgeMs: Long = 30 * 60 * 1000L) {
        val cutoff = System.currentTimeMillis() - maxAgeMs
        preloadDir.listFiles()?.forEach { file ->
            if (file.name.endsWith(".tmp") || file.lastModified() < cutoff) {
                file.delete()
                Log.d(TAG, "evictPreloadCache: deleted ${file.name}")
            }
        }
    }

    // --- Internal ---

    private suspend fun downloadToFile(trackId: Long, targetFile: File): Result<File> {
        // Шаг 1-3: resolve ephemeral URL
        val urlResult = playerService.getTrackUrl(trackId)
        if (urlResult.isFailure) {
            Log.e(TAG, "downloadToFile $trackId: URL resolution failed", urlResult.exceptionOrNull())
            return Result.failure(urlResult.exceptionOrNull()!!)
        }

        val trackUrl = urlResult.getOrNull()!!
        Log.d(TAG, "downloadToFile $trackId: downloading from ${trackUrl.url.take(60)}...")

        val tmpFile = File(targetFile.parent, "${targetFile.name}.tmp")
        return try {
            val request = Request.Builder().url(trackUrl.url).build()
            val response = awaitCall(httpClient.newCall(request))

            if (!response.isSuccessful) {
                response.close()
                return Result.failure(Exception("HTTP ${response.code}"))
            }

            response.body?.byteStream()?.use { input ->
                tmpFile.outputStream().use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        coroutineContext.ensureActive()
                        output.write(buffer, 0, bytesRead)
                    }
                }
            } ?: run {
                response.close()
                return Result.failure(Exception("Empty response body"))
            }

            tmpFile.renameTo(targetFile)
            Log.d(TAG, "downloadToFile $trackId: complete, size=${targetFile.length()}")
            Result.success(targetFile)
        } catch (e: CancellationException) {
            Log.d(TAG, "downloadToFile $trackId: cancelled")
            tmpFile.delete()
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "downloadToFile $trackId: failed", e)
            tmpFile.delete()
            Result.failure(e)
        }
    }

    /** OkHttp Call → suspend с автоматической отменой при cancellation корутины. */
    private suspend fun awaitCall(call: Call): Response =
        suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    cont.resume(response) { response.close() }
                }
                override fun onFailure(call: Call, e: IOException) {
                    if (!cont.isCancelled) cont.resumeWithException(e)
                }
            })
        }

    // TODO: progress callback Flow<Float> для UI индикации скачивания
    // TODO: LRU eviction для preload cache с настраиваемым max size
    // TODO: WiFi-only режим скачивания через ConnectivityManager
    // TODO: Concurrent download queue с настраиваемым parallelism
}
