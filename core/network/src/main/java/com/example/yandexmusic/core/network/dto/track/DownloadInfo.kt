package com.example.yandexmusic.core.network.dto.track

import com.google.gson.annotations.SerializedName

/**
 * Информация о вариантах скачивания трека
 * GET /tracks/{trackId}/download-info
 */
data class DownloadInfoResponse(
    @SerializedName("result")
    val result: List<DownloadInfo>?
)

data class DownloadInfo(
    /** Кодек: mp3, aac, flac */
    @SerializedName("codec")
    val codec: String,

    /** Битрейт: 64, 128, 192, 320 */
    @SerializedName("bitrateInKbps")
    val bitrateInKbps: Int,

    /** URL для получения XML с параметрами загрузки */
    @SerializedName("downloadInfoUrl")
    val downloadInfoUrl: String,

    /** Прямая ссылка (обычно false) */
    @SerializedName("direct")
    val direct: Boolean
)

/**
 * XML ответ с параметрами для построения прямой ссылки
 * Парсится из downloadInfoUrl
 */
data class DownloadInfoXml(
    val host: String,
    val path: String,
    val ts: String,
    val s: String
)

/**
 * Готовая ссылка на аудио файл
 */
data class TrackUrl(
    val url: String,
    val codec: String,
    val bitrateInKbps: Int
)
