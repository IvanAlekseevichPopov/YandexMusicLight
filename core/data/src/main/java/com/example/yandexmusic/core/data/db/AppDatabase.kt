package com.example.yandexmusic.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DownloadedTrackEntity::class],
    version = 1,
    exportSchema = false // TODO: включить schema export и добавить стратегию миграций
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedTrackDao(): DownloadedTrackDao
}
