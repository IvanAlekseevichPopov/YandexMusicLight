package com.example.yandexmusic.feature.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmusic.core.data.MusicRepository
import com.example.yandexmusic.core.data.PlayerService
import com.example.yandexmusic.core.data.TrackDownloadManager
import com.example.yandexmusic.core.network.ChartTrack
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

data class PlayableTrack(
    val id: Long,
    val title: String
)

data class HomeUiState(
    val isPlaying: Boolean = false,
    val generatedPlaylists: List<GeneratedPlaylist> = emptyList(),
    val chartTracks: List<ChartTrack> = emptyList(),
    val trackList: List<PlayableTrack> = emptyList(),
    val currentTrackIndex: Int = -1,
    val currentTrackUrl: TrackUrl? = null,
    val currentTrackTitle: String? = null,
    val isLoading: Boolean = false,
    val isLoadingTrack: Boolean = false,
    val error: String? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f
) {
    val hasPrevious: Boolean get() = currentTrackIndex > 0
    val hasNext: Boolean get() = currentTrackIndex < trackList.lastIndex
}

class HomeViewModel(
    private val repository: MusicRepository,
    private val playerService: PlayerService,
    private val trackDownloadManager: TrackDownloadManager,
    context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val audioPlayer = AudioPlayer(context)
    private var progressJob: Job? = null
    private var preloadJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "init: starting loadFeed()")

        audioPlayer.onPlaybackStateChanged = { isPlaying ->
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgressUpdates() else stopProgressUpdates()
        }

        audioPlayer.onTrackEnded = {
            val state = _uiState.value
            if (state.hasNext) {
                playNext()
            } else {
                _uiState.update {
                    it.copy(isPlaying = false, progress = 0f, currentPosition = 0L)
                }
                stopProgressUpdates()
            }
        }

        loadFeed()
    }

    fun loadFeed() {
        Log.d(TAG, "loadFeed: starting")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getFeed()
                .onSuccess { feedResult ->
                    val playlists = feedResult.generatedPlaylists ?: emptyList()
                    Log.d(TAG, "loadFeed: success, playlists=${playlists.size}")

                    if (playlists.isEmpty()) {
                        Log.d(TAG, "loadFeed: no playlists, loading chart as fallback")
                        loadChart()
                    } else {
                        val tracks = playlists.firstOrNull()?.data?.tracks
                            ?.mapNotNull { t ->
                                t.id?.let { PlayableTrack(it, t.track?.title ?: "Track $it") }
                            } ?: emptyList()
                        _uiState.update { state ->
                            state.copy(
                                generatedPlaylists = playlists,
                                trackList = tracks,
                                isLoading = false
                            )
                        }
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

    private suspend fun loadChart() {
        repository.getChart()
            .onSuccess { chartResult ->
                val chartTracks = chartResult.chart?.tracks ?: emptyList()
                Log.d(TAG, "loadChart: success, tracks=${chartTracks.size}")
                val playable = chartTracks.mapNotNull { ct ->
                    ct.track?.let { t ->
                        val title = t.artists?.firstOrNull()?.let { "${it.name} — ${t.title}" } ?: t.title
                        PlayableTrack(t.id, title)
                    }
                }
                _uiState.update { state ->
                    state.copy(
                        chartTracks = chartTracks,
                        trackList = playable,
                        isLoading = false
                    )
                }
            }
            .onFailure { exception ->
                Log.e(TAG, "loadChart: failure", exception)
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
            }
    }

    fun playTrack(trackId: Long, trackTitle: String? = null) {
        Log.d(TAG, "playTrack: trackId=$trackId, title=$trackTitle")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTrack = true, error = null) }

            // Проверяем локальный файл (downloaded → preloaded)
            val localUri = trackDownloadManager.getCachedFileUri(trackId)
            if (localUri != null) {
                Log.d(TAG, "playTrack: playing from cache")
                _uiState.update { state ->
                    state.copy(
                        currentTrackUrl = TrackUrl(localUri, "mp3", 192),
                        currentTrackTitle = trackTitle,
                        isLoadingTrack = false
                    )
                }
                audioPlayer.play(localUri)
                triggerPreloadNext()
                return@launch
            }

            // Сетевой путь
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
                    triggerPreloadNext()
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

    private fun triggerPreloadNext() {
        preloadJob?.cancel()
        val state = _uiState.value
        val nextIndex = state.currentTrackIndex + 1
        val nextTrack = state.trackList.getOrNull(nextIndex) ?: return

        preloadJob = viewModelScope.launch {
            Log.d(TAG, "triggerPreloadNext: preloading trackId=${nextTrack.id}")
            trackDownloadManager.preloadTrack(nextTrack.id)
            // TODO: Preload N+2 для ещё более плавного воспроизведения
            // TODO: Preload только по WiFi (ConnectivityManager check)
        }
    }

    fun togglePlayPause() {
        val state = _uiState.value
        if (state.currentTrackUrl == null) {
            playAtIndex(0)
            return
        }

        if (audioPlayer.isPlaying) {
            audioPlayer.pause()
        } else {
            audioPlayer.resume()
        }
    }

    fun playNext() {
        val state = _uiState.value
        val nextIndex = state.currentTrackIndex + 1
        if (nextIndex <= state.trackList.lastIndex) {
            playAtIndex(nextIndex)
        }
    }

    fun playPrevious() {
        val state = _uiState.value
        val prevIndex = state.currentTrackIndex - 1
        if (prevIndex >= 0) {
            playAtIndex(prevIndex)
        }
    }

    private fun playAtIndex(index: Int) {
        val state = _uiState.value
        val track = state.trackList.getOrNull(index) ?: return
        _uiState.update { it.copy(currentTrackIndex = index) }
        playTrack(track.id, track.title)
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
        preloadJob?.cancel()
        stopProgressUpdates()
        audioPlayer.release()
    }
}
