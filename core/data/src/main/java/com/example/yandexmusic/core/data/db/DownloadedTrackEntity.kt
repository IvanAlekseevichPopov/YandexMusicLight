package com.example.yandexmusic.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_tracks")
data class DownloadedTrackEntity(
    @PrimaryKey
    val trackId: Long,
    val title: String,
    val artistName: String?,
    val codec: String,
    val bitrateInKbps: Int,
    val fileName: String,
    val fileSizeBytes: Long,
    val downloadedAt: Long
    // TODO: coverUri — для отображения обложки в оффлайне
    // TODO: durationMs — длительность без ExoPlayer
)
