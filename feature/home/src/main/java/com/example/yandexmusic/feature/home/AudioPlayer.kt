package com.example.yandexmusic.feature.home

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(context: Context) {

    companion object {
        private const val TAG = "AudioPlayer"
    }

    private val exoPlayer = ExoPlayer.Builder(context).build()

    var onPlaybackStateChanged: ((isPlaying: Boolean) -> Unit)? = null
    var onTrackEnded: (() -> Unit)? = null

    val isPlaying: Boolean get() = exoPlayer.isPlaying
    val currentPosition: Long get() = exoPlayer.currentPosition
    val duration: Long get() = exoPlayer.duration.coerceAtLeast(0L)

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                Log.d(TAG, "onIsPlayingChanged: $playing")
                onPlaybackStateChanged?.invoke(playing)
            }

            override fun onPlaybackStateChanged(state: Int) {
                Log.d(TAG, "onPlaybackStateChanged: state=$state")
                if (state == Player.STATE_ENDED) {
                    onTrackEnded?.invoke()
                }
            }
        })
    }

    fun play(url: String) {
        Log.d(TAG, "play: ${url.take(80)}...")
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun resume() {
        exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun release() {
        Log.d(TAG, "release")
        exoPlayer.stop()
        exoPlayer.release()
    }
}
