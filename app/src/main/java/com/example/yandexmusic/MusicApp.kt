package com.example.yandexmusic

import android.app.Application
import androidx.room.Room
import com.example.yandexmusic.core.data.PlayerService
import com.example.yandexmusic.core.data.MusicRepository
import com.example.yandexmusic.core.data.TrackDownloadManager
import com.example.yandexmusic.core.data.db.AppDatabase
import com.example.yandexmusic.core.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MusicApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "yandex_music_db").build()
    }

    val repository by lazy { MusicRepository() }
    val playerService by lazy { PlayerService(repository) }

    private val appScope = CoroutineScope(SupervisorJob())

    val trackDownloadManager: TrackDownloadManager by lazy {
        TrackDownloadManager(
            context = this,
            playerService = playerService,
            dao = database.downloadedTrackDao(),
            httpClient = ApiClient.downloadClient,
            scope = appScope
        )
    }
}
