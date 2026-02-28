package com.example.yandexmusic.core.data

import android.util.Log
import com.example.yandexmusic.core.network.dto.track.DownloadInfo
import com.example.yandexmusic.core.network.dto.track.DownloadInfoXml
import com.example.yandexmusic.core.network.dto.track.TrackUrl
import java.security.MessageDigest

/**
 * Сервис для получения прямых ссылок на аудио треки
 *
 * Алгоритм (3 шага):
 * 1. GET /tracks/{trackId}/download-info → список вариантов (codec, bitrate, url)
 * 2. GET {downloadInfoUrl} → XML с параметрами (host, path, ts, s)
 * 3. Построить прямую ссылку: https://{host}/get-mp3/{sign}/{ts}{path}
 *    где sign = MD5(SIGN_SALT + path[1:] + s)
 */
class PlayerService(private val repository: MusicRepository) {

    companion object {
        private const val TAG = "PlayerService"
        private const val SIGN_SALT = "XGRlBW9FXlekgbPrRHuSiA"
    }

    /**
     * Получить прямую ссылку на аудио трек
     *
     * @param trackId ID трека
     * @param preferredCodec предпочтительный кодек (mp3, aac, flac)
     * @param preferredBitrate предпочтительный битрейт (128, 192, 320)
     * @return TrackUrl с прямой ссылкой или ошибка
     */
    suspend fun getTrackUrl(
        trackId: Long,
        preferredCodec: String = "mp3",
        preferredBitrate: Int = 192
    ): Result<TrackUrl> {
        Log.d(TAG, "getTrackUrl: trackId=$trackId, codec=$preferredCodec, bitrate=$preferredBitrate")

        // Шаг 1: Получить варианты скачивания
        val downloadInfoResult = repository.getDownloadInfo(trackId)
        if (downloadInfoResult.isFailure) {
            Log.e(TAG, "Step 1 failed: ${downloadInfoResult.exceptionOrNull()?.message}")
            return Result.failure(downloadInfoResult.exceptionOrNull()!!)
        }

        val downloadInfoList = downloadInfoResult.getOrNull()
        Log.d(TAG, "Step 1 success: ${downloadInfoList?.size} options")
        downloadInfoList?.forEach {
            Log.d(TAG, "  - ${it.codec} ${it.bitrateInKbps}kbps: ${it.downloadInfoUrl.take(50)}...")
        }

        if (downloadInfoList.isNullOrEmpty()) {
            Log.e(TAG, "No download options available")
            return Result.failure(Exception("No download options available"))
        }

        // Выбрать лучший вариант
        val selectedInfo = selectBestDownloadInfo(downloadInfoList, preferredCodec, preferredBitrate)
        if (selectedInfo == null) {
            Log.e(TAG, "No suitable download option found")
            return Result.failure(Exception("No suitable download option found"))
        }
        Log.d(TAG, "Selected: ${selectedInfo.codec} ${selectedInfo.bitrateInKbps}kbps")

        // Шаг 2: Получить XML с параметрами
        val xmlResult = repository.getDownloadInfoXml(selectedInfo.downloadInfoUrl)
        if (xmlResult.isFailure) {
            Log.e(TAG, "Step 2 failed: ${xmlResult.exceptionOrNull()?.message}")
            return Result.failure(xmlResult.exceptionOrNull()!!)
        }

        val xmlString = xmlResult.getOrNull()
        if (xmlString == null) {
            Log.e(TAG, "Empty XML response")
            return Result.failure(Exception("Empty XML response"))
        }
        Log.d(TAG, "Step 2 success: XML length=${xmlString.length}")

        // Парсим XML
        val xmlParams = parseDownloadInfoXml(xmlString)
        if (xmlParams == null) {
            Log.e(TAG, "Failed to parse XML: ${xmlString.take(200)}")
            return Result.failure(Exception("Failed to parse download info XML"))
        }
        Log.d(TAG, "XML parsed: host=${xmlParams.host}, path=${xmlParams.path.take(30)}...")

        // Шаг 3: Построить прямую ссылку
        val directUrl = buildDirectUrl(xmlParams)
        Log.d(TAG, "Step 3 success: directUrl=${directUrl.take(80)}...")

        return Result.success(
            TrackUrl(
                url = directUrl,
                codec = selectedInfo.codec,
                bitrateInKbps = selectedInfo.bitrateInKbps
            )
        )
    }

    /**
     * Выбрать лучший вариант скачивания
     */
    private fun selectBestDownloadInfo(
        list: List<DownloadInfo>,
        preferredCodec: String,
        preferredBitrate: Int
    ): DownloadInfo? {
        // Сначала ищем точное совпадение
        list.find { it.codec == preferredCodec && it.bitrateInKbps == preferredBitrate }
            ?.let { return it }

        // Ищем по кодеку с максимальным битрейтом
        list.filter { it.codec == preferredCodec }
            .maxByOrNull { it.bitrateInKbps }
            ?.let { return it }

        // Берём любой с максимальным битрейтом
        return list.maxByOrNull { it.bitrateInKbps }
    }

    /**
     * Парсинг XML ответа
     *
     * Формат:
     * <download-info>
     *   <host>...</host>
     *   <path>...</path>
     *   <ts>...</ts>
     *   <s>...</s>
     * </download-info>
     */
    private fun parseDownloadInfoXml(xml: String): DownloadInfoXml? {
        return try {
            val host = extractXmlTag(xml, "host") ?: return null
            val path = extractXmlTag(xml, "path") ?: return null
            val ts = extractXmlTag(xml, "ts") ?: return null
            val s = extractXmlTag(xml, "s") ?: return null

            DownloadInfoXml(host, path, ts, s)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractXmlTag(xml: String, tag: String): String? {
        val regex = "<$tag>(.+?)</$tag>".toRegex()
        return regex.find(xml)?.groupValues?.getOrNull(1)
    }

    /**
     * Построить прямую ссылку на аудио файл
     *
     * Формула: https://{host}/get-mp3/{sign}/{ts}{path}
     * где sign = MD5(SIGN_SALT + path[1:] + s)
     */
    private fun buildDirectUrl(params: DownloadInfoXml): String {
        // path без первого символа "/"
        val pathWithoutSlash = if (params.path.startsWith("/")) {
            params.path.substring(1)
        } else {
            params.path
        }

        // Вычисляем подпись
        val signData = SIGN_SALT + pathWithoutSlash + params.s
        val sign = md5(signData)

        return "https://${params.host}/get-mp3/$sign/${params.ts}${params.path}"
    }

    /**
     * MD5 хеш строки
     */
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
