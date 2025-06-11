package lt.vitalijus.records.record.data.audio

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.records.record.domain.audio.AudioPlayer
import lt.vitalijus.records.record.domain.audio.AudioTrack
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class AndroidAudioPlayer(
    private val applicationScope: CoroutineScope
) : AudioPlayer {

    private var currentFilePath: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared: Boolean = false

    private val _activeTrack = MutableStateFlow(AudioTrack())
    override val activeTrack
        get() = _activeTrack.asStateFlow()

    private var durationJob: Job? = null

    override fun play(
        filePath: String,
        onComplete: () -> Unit
    ) {
        val shouldInitializeNewPlayer =
            mediaPlayer == null || filePath != currentFilePath || !isMediaPlayerPrepared

        if (shouldInitializeNewPlayer) {
            stop()
            initMediaPlayer(
                filePath = filePath,
                onComplete = onComplete
            )
        }

        mediaPlayer?.let { player ->
            try {
                player.start()
                _activeTrack.update {
                    it.copy(
                        isPlaying = true
                    )
                }
                trackDuration()
            } catch (e: IllegalStateException) {
                Timber.e(e, "Error starting MediaPlayer, possibly in a bad state.")
                _activeTrack.update {
                    it.copy(
                        isPlaying = false
                    )
                }
            }
        }
    }

    override fun pause() {
        if (!activeTrack.value.isPlaying) {
            return
        }

        _activeTrack.update {
            it.copy(
                isPlaying = false
            )
        }

        durationJob?.cancel()
        mediaPlayer?.pause()
    }

    override fun resume() {
        if (activeTrack.value.isPlaying || !isMediaPlayerPrepared) {
            return
        }

        _activeTrack.update {
            it.copy(
                isPlaying = true
            )
        }

        mediaPlayer?.start()
        trackDuration()
    }

    override fun stop() {
        _activeTrack.update {
            it.copy(
                isPlaying = false,
                durationPlayed = ZERO
            )
        }
        durationJob?.cancel()
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
                reset()
                release()
            } catch (e: IllegalStateException) {
                Timber.e(e, "An error occurred during MediaPlayer stop/reset/release.")
            }
        }
        mediaPlayer = null
        isMediaPlayerPrepared = false
        currentFilePath = null
    }

    override fun seekTo(
        filePath: String,
        onComplete: () -> Unit,
        progress: Float
    ) {
        val shouldInitializeNewPlayer =
            mediaPlayer == null || filePath != currentFilePath || !isMediaPlayerPrepared

        if (shouldInitializeNewPlayer) {
            stop()
            initMediaPlayer(
                filePath = filePath,
                onComplete = onComplete
            )
        }

        mediaPlayer?.let { player ->
            if (isMediaPlayerPrepared) {
                val targetPosition = (player.duration * progress).toInt().coerceAtLeast(0)
                player.seekTo(targetPosition)

                _activeTrack.update {
                    it.copy(
                        durationPlayed = targetPosition.milliseconds
                    )
                }
            } else {
                Timber.w("MediaPlayer not prepared, cannot seek.")
            }
        } ?: run {
            Timber.w("MediaPlayer not initialised, cannot seek.")
        }
    }

    private fun initMediaPlayer(
        filePath: String,
        onComplete: () -> Unit
    ) {
        mediaPlayer = MediaPlayer().apply {
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(File(filePath))
                setDataSource(fileInputStream.fd)
                currentFilePath = filePath

                prepare()
                isMediaPlayerPrepared = true

                setOnCompletionListener {
                    isMediaPlayerPrepared = false
                    onComplete()
                    stop()
                }

                setOnErrorListener { mp, what, extra ->
                    Timber.e("MediaPlayer $mp error: what=$what, extra=$extra for $currentFilePath")
                    isMediaPlayerPrepared = false
                    _activeTrack.update {
                        it.copy(
                            isPlaying = false
                        )
                    }
                    true
                }
            } catch (e: Exception) {
                Timber.e(e, "Error playing audio.")
                isMediaPlayerPrepared = false
                currentFilePath = null
            } finally {
                fileInputStream?.close()
            }
        }
    }

    private fun trackDuration() {
        durationJob?.cancel()
        durationJob = applicationScope.launch {
            do {
                _activeTrack.update {
                    it.copy(
                        totalDuration = mediaPlayer?.duration?.milliseconds ?: ZERO,
                        durationPlayed = mediaPlayer?.currentPosition?.milliseconds ?: ZERO
                    )
                }
                delay(10L)
            } while (activeTrack.value.isPlaying && mediaPlayer?.isPlaying == true)
        }
    }
}
