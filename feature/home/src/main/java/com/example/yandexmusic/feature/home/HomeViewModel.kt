package com.example.yandexmusic.feature.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmusic.core.data.MusicRepository
import com.example.yandexmusic.core.data.PlayerService
import com.example.yandexmusic.core.network.dto.feed.GeneratedPlaylist
import com.example.yandexmusic.core.network.dto.track.TrackUrl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HomeUiState(
    val isPlaying: Boolean = false,
    val generatedPlaylists: List<GeneratedPlaylist> = emptyList(),
    val currentTrackUrl: TrackUrl? = null,
    val currentTrackTitle: String? = null,
    val isLoading: Boolean = false,
    val isLoadingTrack: Boolean = false,
    val error: String? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f
)

class HomeViewModel(
    private val repository: MusicRepository,
    private val playerService: PlayerService,
    context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val audioPlayer = AudioPlayer(context)
    private var progressJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "init: starting loadFeed()")

        audioPlayer.onPlaybackStateChanged = { isPlaying ->
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgressUpdates() else stopProgressUpdates()
        }

        audioPlayer.onTrackEnded = {
            _uiState.update {
                it.copy(isPlaying = false, progress = 0f, currentPosition = 0L)
            }
            stopProgressUpdates()
        }

        loadFeed()
    }

    fun loadFeed() {
        Log.d(TAG, "loadFeed: starting")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getFeed()
                .onSuccess { feedResult ->
                    Log.d(TAG, "loadFeed: success, playlists=${feedResult.generatedPlaylists?.size}")
                    _uiState.update { state ->
                        state.copy(
                            generatedPlaylists = feedResult.generatedPlaylists ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "loadFeed: failure", exception)
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun playTrack(trackId: Long, trackTitle: String? = null) {
        Log.d(TAG, "playTrack: trackId=$trackId, title=$trackTitle")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTrack = true, error = null) }

            playerService.getTrackUrl(trackId)
                .onSuccess { trackUrl ->
                    Log.d(TAG, "playTrack: success, url=${trackUrl.url.take(80)}...")
                    _uiState.update { state ->
                        state.copy(
                            currentTrackUrl = trackUrl,
                            currentTrackTitle = trackTitle,
                            isLoadingTrack = false
                        )
                    }
                    audioPlayer.play(trackUrl.url)
                }
                .onFailure { exception ->
                    Log.e(TAG, "playTrack: failure", exception)
                    _uiState.update { state ->
                        state.copy(
                            isLoadingTrack = false,
                            error = "Не удалось загрузить трек: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun togglePlayPause() {
        val state = _uiState.value
        if (state.currentTrackUrl == null) {
            // Нет текущего трека — берём первый из первого плейлиста
            val firstTrack = state.generatedPlaylists
                .firstOrNull()?.data?.tracks?.firstOrNull()
            if (firstTrack != null) {
                val playlistTitle = state.generatedPlaylists.firstOrNull()?.data?.title
                playTrack(firstTrack.id ?: return, playlistTitle)
            }
            return
        }

        if (audioPlayer.isPlaying) {
            audioPlayer.pause()
        } else {
            audioPlayer.resume()
        }
    }

    fun stop() {
        audioPlayer.pause()
        _uiState.update {
            it.copy(
                isPlaying = false,
                currentTrackUrl = null,
                currentTrackTitle = null,
                progress = 0f,
                currentPosition = 0L,
                duration = 0L
            )
        }
        stopProgressUpdates()
    }

    fun seekTo(progress: Float) {
        val duration = audioPlayer.duration
        if (duration > 0) {
            audioPlayer.seekTo((progress * duration).toLong())
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = viewModelScope.launch {
            while (isActive) {
                val position = audioPlayer.currentPosition
                val duration = audioPlayer.duration
                val progress = if (duration > 0) position.toFloat() / duration else 0f
                _uiState.update {
                    it.copy(
                        currentPosition = position,
                        duration = duration,
                        progress = progress.coerceIn(0f, 1f)
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        audioPlayer.release()
    }
}
