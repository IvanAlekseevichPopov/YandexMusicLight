package com.example.yandexmusic.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmusic.core.data.TrackDownloadManager
import com.example.yandexmusic.core.data.db.DownloadedTrackEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val trackDownloadManager: TrackDownloadManager
) : ViewModel() {

    val downloadedTracks: StateFlow<List<DownloadedTrackEntity>> =
        trackDownloadManager.getDownloadedTracks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeDownload(trackId: Long) {
        viewModelScope.launch {
            trackDownloadManager.removeDownload(trackId)
        }
    }

    // TODO: batch download — скачать все треки плейлиста
    // TODO: управление хранилищем (total size, clear all)
}
